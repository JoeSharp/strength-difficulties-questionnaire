use async_trait::async_trait;
use sdq_model::{Assessor, DemographicFilter, Goal, GoalProgress, GoalType, SdqError};
use sdq_service::GoalService;
use sqlx::PgPool;

pub struct GoalServiceSqlxImpl {
    pub pool: PgPool,
}

impl GoalServiceSqlxImpl {
    pub fn new(pool: PgPool) -> GoalServiceSqlxImpl {
        GoalServiceSqlxImpl { pool }
    }
}

#[async_trait]
impl GoalService for GoalServiceSqlxImpl {
    async fn save_goal(&self, goal: Goal) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn get_for_client(&self, client_id: &str) -> Result<Vec<Goal>, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn delete_all_goals(&self) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn get_goals_with_progress(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        min_progress: i32,
        goal_types: &Vec<GoalType>,
        from: &chrono::NaiveDate,
        to: &chrono::NaiveDate,
    ) -> Result<Vec<GoalProgress>, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn get_goals_with_progress_for_client(
        &self,
        client_id: &str,
        assessors: Assessor,
    ) -> Result<Vec<GoalProgress>, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn get_goal_progress(
        &self,
        goal_id: &str,
        assessor: Assessor,
    ) -> Result<GoalProgress, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn update_goal(&self, goal: Goal) -> Result<Goal, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
    async fn get_goal(&self, goal_id: &str) -> Result<Goal, SdqError> {
        Err(SdqError::NotImplemented(
            "delete_client is not implemented yet".to_string(),
        ))
    }
}
