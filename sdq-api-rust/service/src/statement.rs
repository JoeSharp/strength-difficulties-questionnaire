use async_trait::async_trait;
use sdq_model::{Category, SdqError, Statement};

#[async_trait]
pub trait StatementService {
    async fn get_statement(&self, key: String) -> Result<Statement, SdqError>;
    async fn get_statements(&self) -> Result<Vec<Statement>, SdqError>;
    async fn get_categories(&self) -> Result<Vec<Category>, SdqError>;
}
