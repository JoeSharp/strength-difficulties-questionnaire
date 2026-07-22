use async_trait::async_trait;
use sdq_model::{Category, SdqError, Statement};
use sdq_service::statement::StatementService;
use sqlx::PgPool;

pub struct StatementServiceSqlxImpl {
    pub pool: PgPool,
}

impl StatementServiceSqlxImpl {
    pub fn new(pool: PgPool) -> StatementServiceSqlxImpl {
        StatementServiceSqlxImpl { pool }
    }
}

#[async_trait]
impl StatementService for StatementServiceSqlxImpl {
    async fn get_statement(&self, key: String) -> Result<Statement, SdqError> {
        Err(SdqError::NotImplemented)
    }

    async fn get_statements(&self) -> Result<Vec<Statement>, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_categories(&self) -> Result<Vec<Category>, SdqError> {
        Err(SdqError::NotImplemented)
    }
}
