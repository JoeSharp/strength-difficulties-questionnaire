use axum::Router;

use crate::build_api::AppState;

pub fn build_sdq_api() -> Router<AppState> {
    return Router::new();
}
