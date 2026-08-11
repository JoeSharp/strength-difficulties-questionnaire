use async_trait::async_trait;
use sdq_model::{
    Assessor, DemographicFilter, GboSubmission, Goal, GoalProgress, GoalType, SdqError,
};
use sdq_service::goal::GoalService;
use sqlx::PgPool;
use uuid::Uuid;

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
    async fn save_goal(&self, _goal: Goal) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_for_client(&self, _client_id: &Uuid) -> Result<Vec<Goal>, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn delete_all_goals(&self) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_goals_with_progress(
        &self,
        _assessors: &Vec<Assessor>,
        _filters: &Vec<DemographicFilter>,
        _min_progress: u64,
        _goal_types: &Vec<GoalType>,
        _from: &chrono::NaiveDate,
        _to: &chrono::NaiveDate,
    ) -> Result<Vec<GoalProgress>, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_goals_with_progress_for_client(
        &self,
        _client_id: &Uuid,
        _assessors: Assessor,
    ) -> Result<Vec<GoalProgress>, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_goal_progress(
        &self,
        _goal_id: &Uuid,
        _assessor: Assessor,
    ) -> Result<GoalProgress, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn update_goal(&self, _goal: Goal) -> Result<Goal, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_goal(&self, _goal_id: &Uuid) -> Result<Goal, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn submit_gbo(&self, _submission: GboSubmission) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
}
