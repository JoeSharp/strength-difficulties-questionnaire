use std::sync::Arc;

use axum::Router;
use sdq_service::ClientService;

use crate::{client_api::build_client_api, reference_api::build_reference_api};

#[derive(Clone)]
pub struct AppState {
    pub client_service: Arc<dyn ClientService + Send + Sync>,
}

pub fn build_api(state: AppState) -> Router {
    let client_api = build_client_api();
    let reference_api = build_reference_api();

    Router::new()
        .nest("/reference", reference_api)
        .nest("/client", client_api)
        .with_state(state)
}
