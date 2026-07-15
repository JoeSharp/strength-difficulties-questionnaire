use async_trait::async_trait;
use sdq_model::{
    Assessor, DemographicField, DemographicFilter, DemographicReport, GboSubmission, Goal,
    GoalProgress, GoalType, SdqClient, SdqError,
};

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
        filters: Vec<DemographicFilter>,
    ) -> Result<Vec<SdqClient>, SdqError>;
    async fn get_client_by_id(&self, client_id: &str) -> Result<SdqClient, SdqError>;
    async fn create_client(&self, client: SdqClient) -> Result<SdqClient, SdqError>;
    async fn update_client(&self, client: SdqClient) -> Result<SdqClient, SdqError>;
    async fn delete_client(&self, client_id: &str) -> Result<(), SdqError>;
    async fn delete_all_clients(&self) -> Result<(), SdqError>;
}

#[async_trait]
pub trait GboService {
    async fn save_gbo(&self, submission: GboSubmission) -> Result<(), SdqError>;
    async fn delete_all_gbos(&self) -> Result<(), SdqError>;
}

#[async_trait]
pub trait GoalService {
    async fn save_goal(&self, goal: Goal) -> Result<(), SdqError>;
    async fn get_for_client(&self, client_id: &str) -> Result<Vec<Goal>, SdqError>;
    async fn delete_all_goals(&self) -> Result<(), SdqError>;
    async fn get_goals_with_progress(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        min_progress: i32,
        goal_types: &Vec<GoalType>,
        from: &chrono::NaiveDate,
        to: &chrono::NaiveDate,
    ) -> Result<Vec<GoalProgress>, SdqError>;
    async fn get_goals_with_progress_for_client(
        &self,
        client_id: &str,
        assessors: Assessor,
    ) -> Result<Vec<GoalProgress>, SdqError>;
    async fn get_goal_progress(
        &self,
        goal_id: &str,
        assessor: Assessor,
    ) -> Result<GoalProgress, SdqError>;
    async fn update_goal(&self, goal: Goal) -> Result<Goal, SdqError>;
    async fn get_goal(&self, goal_id: &str) -> Result<Goal, SdqError>;
}
