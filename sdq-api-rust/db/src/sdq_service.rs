use async_trait::async_trait;
use chrono::NaiveDate;
use sdq_model::{
    Assessor, DemographicFilter, ReportingPeriod, SdqError, SdqProgressSummary, SdqSubmission,
    SdqSubmissionSummary,
};
use sdq_service::sdq::SdqService;
use sqlx::PgPool;
use std::collections::HashMap;
use uuid::Uuid;

pub struct SdqServiceSqlxImpl {
    pub pool: PgPool,
}

impl SdqServiceSqlxImpl {
    pub fn new(pool: PgPool) -> SdqServiceSqlxImpl {
        SdqServiceSqlxImpl { pool }
    }
}

#[async_trait]
impl SdqService for SdqServiceSqlxImpl {
    async fn record_response(&self, sdq: SdqSubmission) -> Result<(), SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_submission(
        &self,
        period_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqSubmission, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_summary(
        &self,
        period_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqSubmissionSummary, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn get_reporting_periods(
        &self,
        client_id: &Uuid,
    ) -> Result<Vec<ReportingPeriod>, SdqError> {
        Err(SdqError::NotImplemented)
    }
    async fn query_sdq_progress(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        from: &NaiveDate,
        to: &NaiveDate,
    ) -> Result<Vec<SdqProgressSummary>, SdqError> {
        Err(SdqError::NotImplemented)
    }

    async fn query_sdq_summaries(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        from: &NaiveDate,
        to: &NaiveDate,
    ) -> Result<HashMap<Uuid, HashMap<NaiveDate, Vec<SdqSubmissionSummary>>>, SdqError> {
        Err(SdqError::NotImplemented)
    }

    async fn get_sdq_progress_for_client(
        &self,
        client_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqProgressSummary, SdqError> {
        Err(SdqError::NotImplemented)
    }
}
