use async_trait::async_trait;
use chrono::NaiveDate;
use sdq_model::{
    Assessor, Category, DemographicFilter, Posture, ReportingPeriod, SdqError, SdqProgressSummary,
    SdqScore, SdqSubmission, SdqSubmissionSummary, Statement,
};
use sdq_service::sdq::SdqService;
use sqlx::PgPool;
use std::{collections::HashMap, str::FromStr};
use uuid::Uuid;

pub struct SdqServiceSqlxImpl {
    pub pool: PgPool,
}

impl SdqServiceSqlxImpl {
    pub fn new(pool: PgPool) -> SdqServiceSqlxImpl {
        SdqServiceSqlxImpl { pool }
    }
}
#[derive(sqlx::FromRow)]
struct SdqSubmissionRow {
    score: i64,
    order: i64,
    statement_key: String,
    category: String,
    posture: String,
    description: Option<String>,
    is_true_positive: bool,
}

impl From<SdqSubmissionRow> for SdqScore {
    fn from(raw: SdqSubmissionRow) -> Self {
        SdqScore {
            score: raw.score,
            statement: Statement {
                category: Category {
                    category: raw.category,
                    posture: Posture::from_str(&raw.posture).unwrap_or(Posture::Unknown),
                },
                description: raw.description,
                is_true_positive: raw.is_true_positive,
                key: raw.statement_key,
                order: raw.order,
            },
        }
    }
}

#[async_trait]
impl SdqService for SdqServiceSqlxImpl {
    async fn record_response(&self, sdq: SdqSubmission) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_submission(
        &self,
        period_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqSubmission, SdqError> {
        sqlx::query_as::<_, SdqSubmissionRow>(
            r#"
            SELECT
            s.statement as statement_key,
            s.score as score,
            st.order as order,
            st.category as category,
            c.posture as posture,
            s.description as description,
            s.is_true_positive as is_true_positive
          FROM
            sdq s
          INNER JOIN sdq_statement st
          ON s.statement = st.statement_key
          INNER JOIN sdq_category c
          ON st.category = c.category
          WHERE period_id = $1 AND assessor = $2
            "#,
        )
        .bind(period_id)
        .bind(assessor.to_string())
        .fetch_all(&self.pool)
        .await
        .map_err(|e| SdqError::Db(e.to_string()))
        .map(|r| r.into_iter().map(SdqScore::from).collect())
        .map(|scores| SdqSubmission {
            period_id: period_id.clone(),
            assessor: assessor.clone(),
            scores,
        })
    }
    async fn get_summary(
        &self,
        period_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqSubmissionSummary, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_reporting_periods(
        &self,
        client_id: &Uuid,
    ) -> Result<Vec<ReportingPeriod>, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn query_sdq_progress(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        from: &NaiveDate,
        to: &NaiveDate,
    ) -> Result<Vec<SdqProgressSummary>, SdqError> {
        Err(SdqError::NotImplemented)
    }

    async fn query_sdq_summaries(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        from: &NaiveDate,
        to: &NaiveDate,
    ) -> Result<HashMap<Uuid, HashMap<NaiveDate, Vec<SdqSubmissionSummary>>>, SdqError> {
        Err(SdqError::NotImplemented)
    }

    async fn get_sdq_progress_for_client(
        &self,
        client_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqProgressSummary, SdqError> {
        Err(SdqError::NotImplemented)
    }
}
