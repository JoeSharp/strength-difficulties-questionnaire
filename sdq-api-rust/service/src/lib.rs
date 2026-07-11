use async_trait::async_trait;
use sdq_model::{DemographicFilter, SdqClient, SdqError};

#[async_trait]
pub trait ClientService {
    async fn get_clients(&self) -> Result<Vec<SdqClient>, SdqError>;
    async fn search_clients(
        &self,
        partial_name: Option<String>,
        filters: Vec<DemographicFilter>,
    ) -> Result<Vec<SdqClient>, SdqError>;
}
