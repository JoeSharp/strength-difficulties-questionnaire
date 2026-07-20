use axum::{
    Json, Router,
    extract::{Path, State},
    routing::{get, post, put},
};
use chrono::NaiveDate;
use sdq_model::{Assessor, DemographicFilter, GboSubmission, Goal, GoalProgress, GoalType};
use uuid::Uuid;

use crate::{
    api_error::AppError::{self},
    build_api::AppState,
};

#[derive(Debug, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
struct GoalQueryDTO {
    assessors: Vec<Assessor>,
    filters: Vec<DemographicFilter>,
    min_progress: u64,
    goal_types: Vec<GoalType>,
    from: NaiveDate,
    to: NaiveDate,
}

async fn get_goals_with_progress(
    state: State<AppState>,
    Json(GoalQueryDTO {
        assessors,
        filters,
        min_progress,
        goal_types,
        from,
        to,
    }): Json<GoalQueryDTO>,
) -> Result<Json<Vec<GoalProgress>>, AppError> {
    state
        .goal_service
        .get_goals_with_progress(&assessors, &filters, min_progress, &goal_types, &from, &to)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_goals_for_client(
    state: State<AppState>,
    Path(client_id): Path<Uuid>,
) -> Result<Json<Vec<Goal>>, AppError> {
    state
        .goal_service
        .get_for_client(&client_id)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_goals_progress_for_client(
    state: State<AppState>,
    Path((client_id, assessor)): Path<(Uuid, Assessor)>,
) -> Result<Json<Vec<GoalProgress>>, AppError> {
    state
        .goal_service
        .get_goals_with_progress_for_client(&client_id, assessor)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_goal(
    state: State<AppState>,
    Path(client_id): Path<Uuid>,
) -> Result<Json<Goal>, AppError> {
    state
        .goal_service
        .get_goal(&client_id)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn get_goal_progress(
    state: State<AppState>,
    Path((goal_id, assessor)): Path<(Uuid, Assessor)>,
) -> Result<Json<GoalProgress>, AppError> {
    state
        .goal_service
        .get_goal_progress(&goal_id, assessor)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn update_goal(
    state: State<AppState>,
    Json(goal): Json<Goal>,
) -> Result<Json<Goal>, AppError> {
    state
        .goal_service
        .update_goal(goal)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn submit_gbo(
    state: State<AppState>,
    Json(submission): Json<GboSubmission>,
) -> Result<(), AppError> {
    state
        .goal_service
        .submit_gbo(submission)
        .await
        .map_err(|e| AppError::Sdq(e))
}

pub fn build_goal_api() -> Router<AppState> {
    return Router::new()
        .route("/query", post(get_goals_with_progress))
        .route("/forClient/{client_id}", get(get_goals_for_client))
        .route(
            "/forClient/{client_id}/progress/{assessor}",
            get(get_goals_progress_for_client),
        )
        .route("/{goal_id}", get(get_goal))
        .route("/{goal_id/progress/{assessor}", get(get_goal_progress))
        .route("/", put(update_goal))
        .route("/score", post(submit_gbo));
}
