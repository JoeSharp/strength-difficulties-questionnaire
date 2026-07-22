use async_trait::async_trait;
use sdq_model::{ParsedFile, SdqError};
use sdq_service::upload::UploadService;
use sqlx::PgPool;

pub struct UploadServiceSqlxImpl {
    pub pool: PgPool,
}

impl UploadServiceSqlxImpl {
    pub fn new(pool: PgPool) -> UploadServiceSqlxImpl {
        UploadServiceSqlxImpl { pool }
    }
}

#[async_trait]
impl UploadService for UploadServiceSqlxImpl {
    async fn ingest_file(&self, filename: String, data: Vec<u8>) -> Result<ParsedFile, SdqError> {
        Err(SdqError::NotImplemented)
    }
}
