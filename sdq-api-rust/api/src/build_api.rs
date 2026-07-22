use std::sync::Arc;

use axum::Router;
use sdq_service::{
    client::ClientService, gbo::GboService, goal::GoalService,
    reporting_period::ReportingPeriodService, sdq::SdqService, statement::StatementService,
    upload::UploadService,
};

use crate::{
    admin_api::build_admin_api, client_api::build_client_api, goal_api::build_goal_api,
    reference_api::build_reference_api, sdq_api::build_sdq_api, upload_api::build_upload_api,
};

#[derive(Clone)]
pub struct AppState {
    pub client_service: Arc<dyn ClientService + Send + Sync>,
    pub gbo_service: Arc<dyn GboService + Send + Sync>,
    pub goal_service: Arc<dyn GoalService + Send + Sync>,
    pub reporting_period_service: Arc<dyn ReportingPeriodService + Send + Sync>,
    pub sdq_service: Arc<dyn SdqService + Send + Sync>,
    pub statement_service: Arc<dyn StatementService + Send + Sync>,
    pub upload_service: Arc<dyn UploadService + Send + Sync>,
}

pub fn build_api(state: AppState) -> Router {
    let admin_api = build_admin_api();
    let client_api = build_client_api();
    let goal_api = build_goal_api();
    let reference_api = build_reference_api();
    let sdq_api = build_sdq_api();
    let upload_api = build_upload_api();

    Router::new()
        .nest("/admin", admin_api)
        .nest("/client", client_api)
        .nest("/goal", goal_api)
        .nest("/reference", reference_api)
        .nest("/sdq", sdq_api)
        .nest("/upload", upload_api)
        .with_state(state)
}
