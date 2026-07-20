use std::str::FromStr;

use axum::extract::Path;
use axum::{Json, extract::State};
use axum::{Router, routing::get, routing::post};
use sdq_model::{DemographicField, DemographicFilter, DemographicReport, SdqClient};

use crate::api_error::AppError;
use crate::build_api::AppState;

#[derive(Debug, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
struct ClientQueryDTO {
    pub partial_name: Option<String>,
    pub filters: Vec<DemographicFilter>,
}

async fn get_demographic_report(
    State(state): State<AppState>,
    Path(demo): Path<String>,
) -> Result<Json<DemographicReport>, AppError> {
    if let Ok(demographic) = DemographicField::from_str(&demo) {
        state
            .client_service
            .get_demographic_report(demographic)
            .await
            .map(Json)
            .map_err(|e| AppError::Sdq(e))
    } else {
        tracing::error!("Invalid demographic field: {}", demo);
        Err(AppError::Value(demo))
    }
}

async fn get_clients(State(state): State<AppState>) -> Result<Json<Vec<SdqClient>>, AppError> {
    state
        .client_service
        .get_clients()
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

async fn search_clients(
    State(state): State<AppState>,
    Json(ClientQueryDTO {
        partial_name,
        filters,
    }): Json<ClientQueryDTO>,
) -> Result<Json<Vec<SdqClient>>, AppError> {
    state
        .client_service
        .search_clients(partial_name, &filters)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

pub fn build_client_api() -> Router<AppState> {
    Router::new()
        .route("/", get(get_clients))
        .route("/search", post(search_clients))
        .route(
            "/demographic_report/{demographic}",
            get(get_demographic_report),
        )
}
