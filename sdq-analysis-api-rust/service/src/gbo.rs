use async_trait::async_trait;
use sdq_model::{GboSubmission, SdqError};

#[async_trait]
pub trait GboService {
    async fn save_gbo(&self, submission: GboSubmission) -> Result<(), SdqError>;
    async fn delete_all_gbos(&self) -> Result<(), SdqError>;
}
