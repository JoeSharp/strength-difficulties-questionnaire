use std::collections::HashMap;

use async_trait::async_trait;
use chrono::NaiveDate;
use sdq_model::{
    Assessor, DemographicFilter, ReportingPeriod, SdqError, SdqProgressSummary, SdqSubmission,
    SdqSubmissionSummary,
};

use uuid::Uuid;
#[async_trait]
pub trait SdqService {
    async fn record_response(&self, sdq: SdqSubmission) -> Result<(), SdqError>;
    async fn get_submission(
        &self,
        period_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqSubmission, SdqError>;
    async fn get_summary(
        &self,
        period_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqSubmissionSummary, SdqError>;
    async fn get_reporting_periods(
        &self,
        client_id: &Uuid,
    ) -> Result<Vec<ReportingPeriod>, SdqError>;
    async fn query_sdq_progress(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        from: &NaiveDate,
        to: &NaiveDate,
    ) -> Result<Vec<SdqProgressSummary>, SdqError>;
    async fn query_sdq_summaries(
        &self,
        assessors: &Vec<Assessor>,
        filters: &Vec<DemographicFilter>,
        from: &NaiveDate,
        to: &NaiveDate,
    ) -> Result<HashMap<Uuid, HashMap<NaiveDate, Vec<SdqSubmissionSummary>>>, SdqError>;
    async fn get_sdq_progress_for_client(
        &self,
        client_id: &Uuid,
        assessor: &Assessor,
    ) -> Result<SdqProgressSummary, SdqError>;
}
