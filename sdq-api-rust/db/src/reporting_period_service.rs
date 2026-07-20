use async_trait::async_trait;
use sdq_model::{ReportingPeriod, SdqError};
use sdq_service::ReportingPeriodService;
use sqlx::PgPool;
use uuid::Uuid;

pub struct ReportingPeriodServiceSqlxImpl {
    pub pool: PgPool,
}

impl ReportingPeriodServiceSqlxImpl {
    pub fn new(pool: PgPool) -> ReportingPeriodServiceSqlxImpl {
        ReportingPeriodServiceSqlxImpl { pool }
    }
}

#[async_trait]
impl ReportingPeriodService for ReportingPeriodServiceSqlxImpl {
    async fn save(&self, submission: ReportingPeriod) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn get_for_client(&self, client_id: &Uuid) -> Result<Vec<ReportingPeriod>, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn delete_all(&self) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
}
