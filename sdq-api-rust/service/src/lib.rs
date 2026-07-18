use std::iter::Map;

use async_trait::async_trait;
use chrono::NaiveDate;
use sdq_model::{
    Assessor, Category, DemographicField, DemographicFilter, DemographicReport, GboSubmission,
    Goal, GoalProgress, GoalType, ReportingPeriod, SdqClient, SdqError, SdqProgressSummary,
    SdqSubmission, SdqSubmissionSummary, Statement,
};
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

#[async_trait]
pub trait ReportingPeriodService {
    async fn save(&self, submission: ReportingPeriod) -> Result<(), SdqError>;
    async fn get_for_client(&self, client_id: &str) -> Result<Vec<ReportingPeriod>, SdqError>;
    async fn delete_all(&self) -> Result<(), SdqError>;
}

#[async_trait]
pub trait StatementService {
    async fn get_statement(&self, key: String) -> Result<Statement, SdqError>;
    async fn get_statements(&self) -> Result<Vec<Statement>, SdqError>;
    async fn get_categories(&self) -> Result<Vec<Category>, SdqError>;
}

#[async_trait]
pub trait SdqService {
    async fn record_response(&self, sdq: SdqSubmission) -> Result<(), SdqError>;
    async fn get_submission(
        &self,
        period_id: Uuid,
        assessor: Assessor,
    ) -> Result<SdqSubmission, SdqError>;
    async fn get_summary(
        &self,
        period_id: Uuid,
        assessor: Assessor,
    ) -> Result<SdqSubmissionSummary, SdqError>;
    async fn get_reporting_periods(
        &self,
        client_id: Uuid,
    ) -> Result<Vec<ReportingPeriod>, SdqError>;
    async fn query_sdq_progress(
        &self,
        assessors: Vec<Assessor>,
        filters: Vec<DemographicFilter>,
        from: NaiveDate,
        to: NaiveDate,
    ) -> Result<Vec<SdqProgressSummary>, SdqError>;
    async fn query_sdq_summaries(
        &self,
        assessors: Vec<Assessor>,
        filters: Vec<DemographicFilter>,
        from: NaiveDate,
        to: NaiveDate,
    ) -> Result<Map<Uuid, Map<NaiveDate, Vec<SdqSubmissionSummary>>>, SdqError>;
    async fn get_sdq_progress_for_client(
        &self,
        client_id: Uuid,
        assessor: Assessor,
    ) -> Result<SdqProgressSummary, SdqError>;
}
