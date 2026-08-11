use axum::{Router, extract::State, routing::delete};

use crate::{api_error::AppError, build_api::AppState};

async fn clear_database(state: State<AppState>) -> Result<(), AppError> {
    return state
        .client_service
        .delete_all_clients()
        .await
        .map_err(|e| AppError::Sdq(e));
}

pub fn build_admin_api() -> Router<AppState> {
    return Router::new().route("/", delete(clear_database));
}
