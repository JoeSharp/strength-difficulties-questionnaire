use axum::{
    Json, Router,
    extract::{Path, State},
    routing::{get, post},
};
use chrono::NaiveDate;
use sdq_model::{
    Assessor, DemographicFilter, ReportingPeriod, SdqProgressSummary, SdqSubmission,
    SdqSubmissionSummary,
};
use std::collections::HashMap;
use uuid::Uuid;

use crate::{
    api_error::AppError::{self},
    build_api::AppState,
};

async fn get_reporting_periods(
    state: State<AppState>,
    Path(client_id): Path<Uuid>,
) -> Result<Json<Vec<ReportingPeriod>>, AppError> {
    state
        .sdq_service
        .get_reporting_periods(&client_id)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_submission(
    state: State<AppState>,
    Path((period_id, assessor)): Path<(Uuid, Assessor)>,
) -> Result<Json<SdqSubmission>, AppError> {
    state
        .sdq_service
        .get_submission(&period_id, &assessor)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_submission_summary(
    state: State<AppState>,
    Path((period_id, assessor)): Path<(Uuid, Assessor)>,
) -> Result<Json<SdqSubmissionSummary>, AppError> {
    state
        .sdq_service
        .get_summary(&period_id, &assessor)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_sdq_progress(
    state: State<AppState>,
    Path((client_id, assessor)): Path<(Uuid, Assessor)>,
) -> Result<Json<SdqProgressSummary>, AppError> {
    state
        .sdq_service
        .get_sdq_progress_for_client(&client_id, &assessor)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

#[derive(Debug, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
struct SdqQueryDTO {
    assessors: Option<Vec<Assessor>>,
    filters: Option<Vec<DemographicFilter>>,
    from: Option<NaiveDate>,
    to: Option<NaiveDate>,
}

async fn query_sdq(
    state: State<AppState>,
    Json(SdqQueryDTO {
        assessors,
        filters,
        from,
        to,
    }): Json<SdqQueryDTO>,
) -> Result<Json<HashMap<Uuid, HashMap<NaiveDate, Vec<SdqSubmissionSummary>>>>, AppError> {
    state
        .sdq_service
        .query_sdq_summaries(
            &assessors.unwrap_or_default(),
            &filters.unwrap_or_default(),
            &from.unwrap_or_default(),
            &to.unwrap_or_default(),
        )
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

fn group_by_client_id(
    summaries: Vec<SdqProgressSummary>,
) -> HashMap<Uuid, Vec<SdqProgressSummary>> {
    let mut grouped: HashMap<Uuid, Vec<SdqProgressSummary>> = HashMap::new();

    for summary in summaries {
        grouped.entry(summary.client_id).or_default().push(summary);
    }

    grouped
}

async fn query_sdq_progress(
    state: State<AppState>,
    Json(SdqQueryDTO {
        assessors,
        filters,
        from,
        to,
    }): Json<SdqQueryDTO>,
) -> Result<Json<HashMap<Uuid, Vec<SdqProgressSummary>>>, AppError> {
    state
        .sdq_service
        .query_sdq_progress(
            &assessors.unwrap_or_default(),
            &filters.unwrap_or_default(),
            &from.unwrap_or_default(),
            &to.unwrap_or_default(),
        )
        .await
        .map(group_by_client_id)
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

pub fn build_sdq_api() -> Router<AppState> {
    return Router::new()
        .route("/{client_id}/reportingPeriods", get(get_reporting_periods))
        .route("/{period_id}/{assessor}", get(get_submission))
        .route(
            "/{period_id}/{assessor}/summary",
            get(get_submission_summary),
        )
        .route("/{client_id}/{assessor}/progress", get(get_sdq_progress))
        .route("/query", post(query_sdq))
        .route("/query/progress", post(query_sdq_progress));
}
