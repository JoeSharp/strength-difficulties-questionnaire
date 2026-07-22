use axum::Json;
use axum::Router;
use axum::extract::Multipart;
use axum::extract::State;
use axum::routing::post;
use sdq_model::ParsedFile;

use crate::api_error::AppError;
use crate::build_api::AppState;

pub async fn upload_files(
    state: State<AppState>,
    mut multipart: Multipart,
) -> Result<Json<Vec<ParsedFile>>, AppError> {
    let mut results = Vec::new();

    while let Some(field) = multipart.next_field().await.map_err(AppError::Multipart)? {
        let filename = field
            .file_name()
            .map(|s| s.to_string())
            .unwrap_or_else(|| "unknown".into());

        let data = field.bytes().await.map_err(AppError::Multipart)?;

        let parsed = state
            .upload_service
            .ingest_file(filename, data.to_vec())
            .await
            .map_err(AppError::Sdq)?;

        results.push(parsed);
    }

    Ok(Json(results))
}

pub fn build_upload_api() -> Router<AppState> {
    return Router::new().route("/upload", post(upload_files));
}
