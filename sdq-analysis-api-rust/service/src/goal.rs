use async_trait::async_trait;
use sdq_model::{
    Assessor, DemographicFilter, GboSubmission, Goal, GoalProgress, GoalType, SdqError,
};
use uuid::Uuid;

#[async_trait]
pub trait GoalService {
    async fn save_goal(&self, goal: Goal) -> Result<(), SdqError>;
    async fn get_for_client(&self, client_id: &Uuid) -> Result<Vec<Goal>, SdqError>;
    async fn delete_all_goals(&self) -> Result<(), SdqError>;
    async fn get_goals_with_progress(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        min_progress: u64,
        goal_types: &Vec<GoalType>,
        from: &chrono::NaiveDate,
        to: &chrono::NaiveDate,
    ) -> Result<Vec<GoalProgress>, SdqError>;
    async fn get_goals_with_progress_for_client(
        &self,
        client_id: &Uuid,
        assessor: Assessor,
    ) -> Result<Vec<GoalProgress>, SdqError>;
    async fn get_goal_progress(
        &self,
        goal_id: &Uuid,
        assessor: Assessor,
    ) -> Result<GoalProgress, SdqError>;
    async fn update_goal(&self, goal: Goal) -> Result<Goal, SdqError>;
    async fn get_goal(&self, goal_id: &Uuid) -> Result<Goal, SdqError>;
    async fn submit_gbo(&self, submission: GboSubmission) -> Result<(), SdqError>;
}
