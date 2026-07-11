use axum::{Json, extract::State};
use axum::{Router, routing::get, routing::post};
use sdq_model::{DemographicFilter, SdqClient};

use crate::build_api::AppState;

#[derive(Debug, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
struct ClientQueryDTO {
    pub partial_name: Option<String>,
    pub filters: Vec<DemographicFilter>,
}

async fn get_clients(State(state): State<AppState>) -> Json<Vec<SdqClient>> {
    let clients = state.client_service.get_clients().await.unwrap();
    Json(clients)
}

async fn search_clients(
    State(state): State<AppState>,
    Json(payload): Json<serde_json::Value>,
) -> Json<Vec<SdqClient>> {
    let payload: ClientQueryDTO = serde_json::from_value(payload).unwrap();
    let clients = state
        .client_service
        .search_clients(payload.partial_name, payload.filters)
        .await
        .unwrap();
    Json(clients)
}

pub fn build_client_api() -> Router<AppState> {
    Router::new()
        .route("/", get(get_clients))
        .route("/search", post(search_clients))
}
