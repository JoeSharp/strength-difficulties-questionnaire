use axum::Router;

use crate::build_api::AppState;

pub fn build_goal_api() -> Router<AppState> {
    return Router::new();
}
