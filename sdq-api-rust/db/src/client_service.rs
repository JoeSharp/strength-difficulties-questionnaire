use async_trait::async_trait;
use chrono::NaiveDate;
use sdq_model::{
    AceType, CareExperience, Council, DemographicFilter, DisabilityStatus, DisabilityType,
    EnglishAsAdditionalLanguage, Ethnicity, FundingSource, Gender, Intervention, SdqClient,
    SdqError,
};
use sdq_service::ClientService;
use serde_json::Value;
use sqlx::{AssertSqlSafe, PgPool};
use std::collections::HashMap;
use std::str::FromStr;
use uuid::Uuid;

use crate::column_name::ColumnName;

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

#[async_trait]
impl ClientService for ClientServiceSqlxImpl {
    async fn get_clients(&self) -> Result<Vec<SdqClient>, SdqError> {
        sqlx::query_as::<_, RawSdqClient>(
            r#"
    SELECT
        client_id,
        code_name,
        date_of_birth,
        gender,
        council,
        ethnicity,
        eal,
        disability_status,
        care_experience,
        funding_source,
        interventions,
        disability_types,
        aces
    FROM client_full
    "#,
        )
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
        // 1. Build SQL string completely
        let sql = {
            let mut s = String::from("SELECT * FROM client_full");
            let mut conditions = Vec::new();
            let mut placeholder = IncrementingIndex::create();

            for filter in &filters {
                let column = filter.field.column();

                let placeholders: Vec<String> = (0..filter.values.len())
                    .map(|_| format!("${}", placeholder.next_index()))
                    .collect();

                conditions.push(format!("{} IN ({})", column, placeholders.join(", ")));
            }

            if partial_name
                .as_ref()
                .filter(|s| !s.trim().is_empty())
                .is_some()
            {
                conditions.push(format!("code_name ILIKE ${}", placeholder.next_index()));
            }

            if !conditions.is_empty() {
                s.push_str(" WHERE ");
                s.push_str(&conditions.join(" AND "));
            }

            // IMPORTANT: return the final string
            s
        };

        // 2. SQL string is now FINAL — no more mutations
        let safe_sql = AssertSqlSafe(sql.as_str());
        let mut query = sqlx::query_as::<_, RawSdqClient>(safe_sql);

        // 3. Bind values
        tracing::info!("Running Query {:?}", sql);
        for filter in &filters {
            tracing::info!("Binding values for filter {:?}", filter);
            for value in &filter.values {
                query = query.bind(value);
            }
        }

        if let Some(name) = partial_name.as_ref().filter(|s| !s.trim().is_empty()) {
            tracing::info!("Binding value for partial_name {:?}", name);
            query = query.bind(format!("%{}%", name));
        }

        // 4. Execute
        query
            .fetch_all(&self.pool)
            .await
            .map_err(|e| {
                tracing::error!("Search query failed: {:?}", e);
                SdqError::InternalError(e.to_string())
            })
            .map(|raw_vec| raw_vec.into_iter().map(SdqClient::from).collect())
    }
}
