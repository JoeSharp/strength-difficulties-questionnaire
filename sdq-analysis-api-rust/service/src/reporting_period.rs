use async_trait::async_trait;
use sdq_model::{ReportingPeriod, SdqError};
use uuid::Uuid;

#[async_trait]
pub trait ReportingPeriodService {
    async fn save(&self, submission: ReportingPeriod) -> Result<(), SdqError>;
    async fn get_for_client(&self, client_id: &Uuid) -> Result<Vec<ReportingPeriod>, SdqError>;
    async fn delete_all(&self) -> Result<(), SdqError>;
}
