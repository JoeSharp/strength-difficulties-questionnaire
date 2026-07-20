use axum::http::StatusCode;
use axum::response::IntoResponse;
use sdq_model::SdqError;

pub enum AppError {
    Sdq(SdqError),
    Value(String),
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
            AppError::Sdq(SdqError::NotImplemented(msg)) => {
                (StatusCode::NOT_IMPLEMENTED, msg).into_response()
            }
            AppError::Value(msg) => (StatusCode::BAD_REQUEST, msg).into_response(),
        }
    }
}
