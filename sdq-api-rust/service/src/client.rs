use async_trait::async_trait;
use sdq_model::{DemographicField, DemographicFilter, DemographicReport, SdqClient, SdqError};
use uuid::Uuid;

#[async_trait]
pub trait ClientService {
    async fn get_demographic_report(
        &self,
        field: DemographicField,
    ) -> Result<DemographicReport, SdqError>;
    async fn get_clients(&self) -> Result<Vec<SdqClient>, SdqError>;
    async fn search_clients(
        &self,
        partial_name: Option<String>,
        filters: &Vec<DemographicFilter>,
    ) -> Result<Vec<SdqClient>, SdqError>;
    async fn get_client_by_id(&self, client_id: &Uuid) -> Result<SdqClient, SdqError>;
    async fn create_client(&self, client: SdqClient) -> Result<SdqClient, SdqError>;
    async fn update_client(&self, client: SdqClient) -> Result<SdqClient, SdqError>;
    async fn delete_client(&self, client_id: &Uuid) -> Result<(), SdqError>;
    async fn delete_all_clients(&self) -> Result<(), SdqError>;
}
