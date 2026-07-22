use async_trait::async_trait;
use sdq_model::{GboSubmission, SdqError};
use sdq_service::gbo::GboService;
use sqlx::PgPool;

pub struct GboServiceSqlxImpl {
    pub pool: PgPool,
}

impl GboServiceSqlxImpl {
    pub fn new(pool: PgPool) -> GboServiceSqlxImpl {
        GboServiceSqlxImpl { pool }
    }
}

#[async_trait]
impl GboService for GboServiceSqlxImpl {
    async fn save_gbo(&self, submission: GboSubmission) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn delete_all_gbos(&self) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
}
