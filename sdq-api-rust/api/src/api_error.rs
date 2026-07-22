use axum::http::StatusCode;
use axum::response::IntoResponse;
use sdq_model::SdqError;

pub enum AppError {
    Sdq(sdq_model::SdqError),
    Value(String),
    Multipart(axum::extract::multipart::MultipartError),
}

impl From<SdqError> for AppError {
    fn from(e: SdqError) -> Self {
        AppError::Sdq(e)
    }
}

impl IntoResponse for AppError {
    fn into_response(self) -> axum::response::Response {
        match self {
            AppError::Sdq(SdqError::InternalError(msg)) => {
                (StatusCode::INTERNAL_SERVER_ERROR, msg).into_response()
            }
            AppError::Sdq(SdqError::InvalidInput(msg)) => {
                (StatusCode::BAD_REQUEST, msg).into_response()
            }
            AppError::Sdq(SdqError::NotImplemented) => {
                (StatusCode::NOT_IMPLEMENTED, "Not implemented").into_response()
            }
            AppError::Multipart(e) => {
                (StatusCode::BAD_REQUEST, format!("Multipart error: {}", e)).into_response()
            }
            AppError::Value(msg) => (StatusCode::BAD_REQUEST, msg).into_response(),
        }
    }
}
