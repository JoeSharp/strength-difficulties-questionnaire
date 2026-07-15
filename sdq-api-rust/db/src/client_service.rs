use async_trait::async_trait;
use chrono::NaiveDate;
use sdq_model::{
    AceType, CareExperience, Council, DemographicCount, DemographicField, DemographicFilter,
    DemographicReport, DisabilityStatus, DisabilityType, EnglishAsAdditionalLanguage, Ethnicity,
    FundingSource, Gender, Intervention, SdqClient, SdqError,
};
use sdq_service::ClientService;
use serde_json::Value;
use sqlx::{AssertSqlSafe, PgPool};
use std::collections::HashMap;
use std::str::FromStr;
use uuid::Uuid;

impl From<RawSdqClient> for SdqClient {
    fn from(raw: RawSdqClient) -> Self {
        let disability_types: Vec<DisabilityType> = raw
            .disability_types
            .and_then(|v| serde_json::from_value(v).ok())
            .unwrap_or_default();
        let interventions: Vec<Intervention> = raw
            .interventions
            .and_then(|v| serde_json::from_value(v).ok())
            .unwrap_or_default();
        let aces: HashMap<AceType, i32> = raw
            .aces
            .and_then(|v| serde_json::from_value(v).ok())
            .unwrap_or_default();
        SdqClient {
            client_id: raw.client_id,
            code_name: raw.code_name,
            date_of_birth: raw.date_of_birth,
            gender: raw.gender.and_then(|g| Gender::from_str(&g).ok()),
            council: raw.council.and_then(|g| Council::from_str(&g).ok()),
            ethnicity: raw.ethnicity.and_then(|e| Ethnicity::from_str(&e).ok()),
            eal: raw
                .eal
                .and_then(|e| EnglishAsAdditionalLanguage::from_str(&e).ok()),
            disability_status: raw
                .disability_status
                .and_then(|d| DisabilityStatus::from_str(&d).ok()),
            care_experience: raw
                .care_experience
                .and_then(|c| CareExperience::from_str(&c).ok()),
            funding_source: raw
                .funding_source
                .and_then(|f| FundingSource::from_str(&f).ok()),
            interventions,
            disability_types,
            aces,
        }
    }
}

use crate::increment_index::IncrementingIndex;

pub struct ClientServiceSqlxImpl {
    pub pool: PgPool,
}

impl ClientServiceSqlxImpl {
    pub fn new(pool: PgPool) -> ClientServiceSqlxImpl {
        ClientServiceSqlxImpl { pool }
    }
}

#[derive(sqlx::FromRow)]
pub struct RawSdqClient {
    pub client_id: Option<Uuid>,
    pub code_name: Option<String>,
    pub date_of_birth: Option<NaiveDate>,
    pub gender: Option<String>,
    pub council: Option<String>,
    pub ethnicity: Option<String>,
    pub eal: Option<String>,
    pub disability_status: Option<String>,
    pub care_experience: Option<String>,
    pub funding_source: Option<String>,
    pub interventions: Option<Value>,
    pub disability_types: Option<Value>,
    pub aces: Option<Value>,
}

#[derive(sqlx::FromRow)]
pub struct RawDemographicCount {
    pub option: String,
    pub count: i64,
    pub percentage: f64,
}

fn demographic_column(field: &DemographicField) -> &'static str {
    match field {
        DemographicField::Gender => "gender",
        DemographicField::Council => "council",
        DemographicField::Ethnicity => "ethnicity",
        DemographicField::EAL => "eal",
        DemographicField::DisabilityStatus => "disability_status",
        DemographicField::DisabilityType => "disability_type",
        DemographicField::CareExperience => "care_experience",
        DemographicField::ACES => "aces",
        DemographicField::FundingSource => "funding_source",
        _ => "foo", // sort this out!
    }
}

#[async_trait]
impl ClientService for ClientServiceSqlxImpl {
    async fn get_demographic_report(
        &self,
        field: DemographicField,
    ) -> Result<DemographicReport, SdqError> {
        let column = demographic_column(&field);
        let sql = &format!(
            "SELECT {} as option, count(*) AS count, (round(100 * count(*) / (select count(*) from client), 2))::FLOAT8 as percentage FROM client GROUP BY {};",
            column, column
        );
        let safe_sql = AssertSqlSafe(sql.as_str());
        sqlx::query_as::<_, RawDemographicCount>(safe_sql)
            .fetch_all(&self.pool)
            .await
            .map_err(|e| {
                tracing::error!("Demographic report query failed: {:?}", e);
                SdqError::InternalError(e.to_string())
            })
            .map(|counts| {
                counts
                    .into_iter()
                    .map(|raw| DemographicCount {
                        option: raw.option,
                        count: raw.count,
                        percentage: raw.percentage,
                    })
                    .collect()
            })
            .map(|counts| DemographicReport { counts })
    }

    async fn get_clients(&self) -> Result<Vec<SdqClient>, SdqError> {
        sqlx::query_as::<_, RawSdqClient>("SELECT * FROM client_full")
            .fetch_all(&self.pool)
            .await
            .map_err(|e| {
                tracing::error!("Search query failed: {:?}", e);
                SdqError::InternalError(e.to_string())
            })
            .map(|raw_vec| raw_vec.into_iter().map(SdqClient::from).collect())
    }

    async fn search_clients(
        &self,
        partial_name: Option<String>,
        filters: Vec<DemographicFilter>,
    ) -> Result<Vec<SdqClient>, SdqError> {
        let (sql, values) = {
            let mut sql = String::from("SELECT * FROM client_full");
            let mut values: Vec<String> = Vec::new();
            let mut conditions = Vec::new();
            let mut placeholder = IncrementingIndex::create();

            for filter in &filters {
                let column = demographic_column(&filter.field);

                let placeholders: Vec<String> = (0..filter.values.len())
                    .map(|_| format!("${}", placeholder.next_index()))
                    .collect();

                conditions.push(format!("{} IN ({})", column, placeholders.join(", ")));
                for value in &filter.values {
                    values.push(value.clone());
                }
            }

            if let Some(name) = partial_name.as_ref().filter(|s| !s.trim().is_empty()) {
                conditions.push(format!("code_name ILIKE ${}", placeholder.next_index()));

                values.push(format!("%{}%", name));
            }

            if !conditions.is_empty() {
                sql.push_str(" WHERE ");
                sql.push_str(&conditions.join(" AND "));
            }

            // IMPORTANT: return the final string
            (sql, values)
        };

        let query = {
            let safe_sql = AssertSqlSafe(sql.as_str());
            let mut q = sqlx::query_as::<_, RawSdqClient>(safe_sql);

            tracing::info!("Running Query {:?}", sql);
            for (i, value) in values.iter().enumerate() {
                tracing::info!("Binding value for placeholder ${}: {:?}", i + 1, value);
                q = q.bind(value);
            }

            q
        };

        query
            .fetch_all(&self.pool)
            .await
            .map_err(|e| {
                tracing::error!("Search query failed: {:?}", e);
                SdqError::InternalError(e.to_string())
            })
            .map(|raw_vec| raw_vec.into_iter().map(SdqClient::from).collect())
    }

    async fn get_client_by_id(&self, client_id: &str) -> Result<SdqClient, SdqError> {
        Err(SdqError::NotImplemented(
            "get_client_by_id is not implemented yet".to_string(),
        ))
    }
    async fn create_client(&self, client: SdqClient) -> Result<SdqClient, SdqError> {
        Err(SdqError::NotImplemented(
            "create_client is not implemented yet".to_string(),
        ))
    }
    async fn update_client(&self, client: SdqClient) -> Result<SdqClient, SdqError> {
        Err(SdqError::NotImplemented(
            "update_client is not implemented yet".to_string(),
        ))
    }
    async fn delete_client(&self, client_id: &str) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn delete_all_clients(&self) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented(
            "delete_all_clients is not implemented yet".to_string(),
        ))
    }
}
