use async_trait::async_trait;
use sdq_model::{ParsedFile, SdqError};

#[async_trait]
pub trait UploadService {
    async fn ingest_file(&self, filename: String, data: Vec<u8>) -> Result<ParsedFile, SdqError>;
}
