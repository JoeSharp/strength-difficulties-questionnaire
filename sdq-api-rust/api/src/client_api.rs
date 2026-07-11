use axum::{Json, extract::State};
use axum::{Router, routing::get, routing::post};
use sdq_model::{DemographicFilter, SdqClient};

use crate::api_error::AppError;
use crate::build_api::AppState;

#[derive(Debug, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
struct ClientQueryDTO {
    pub partial_name: Option<String>,
    pub filters: Vec<DemographicFilter>,
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
    Json(payload): Json<serde_json::Value>,
) -> Result<Json<Vec<SdqClient>>, AppError> {
    let payload: ClientQueryDTO = serde_json::from_value(payload).map_err(|e| AppError::Json(e))?;
    state
        .client_service
        .search_clients(payload.partial_name, payload.filters)
        .await
        .map(Json)
        .map_err(|e| AppError::Sdq(e))
}

pub fn build_client_api() -> Router<AppState> {
    Router::new()
        .route("/", get(get_clients))
        .route("/search", post(search_clients))
}
