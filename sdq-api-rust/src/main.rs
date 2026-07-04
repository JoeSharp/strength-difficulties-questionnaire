use axum::{Json, extract::State};
use axum::{Router, routing::get};
use dotenvy::dotenv;
use serde::Serialize;
use sqlx::PgPool;
use sqlx::postgres::PgPoolOptions;
use std::env;
use tower_http::services::ServeDir;

#[derive(Serialize)]
struct Greeting {
    message: String,
}

async fn get_clients(State(pool): State<PgPool>) -> Json<Greeting> {
    let row: (String,) = sqlx::query_as("SELECT code_name from client")
        .fetch_one(&pool)
        .await
        .unwrap();

    Json(Greeting { message: row.0 })
}

#[tokio::main]
async fn main() {
    dotenv().ok(); // load .env

    let user = env::var("SDQ_DATABASE_USERNAME").expect("SDQ_DATABASE_USERNAME not in env");
    let pass = env::var("SDQ_DATABASE_PASSWORD").expect("SDQ_DATABASE_PASSWORD not in env");
    let host = env::var("SDQ_DATABASE_HOST").expect("SDQ_DATABASE_HOST not in env");
    let name = env::var("SDQ_DATABASE_NAME").expect("SDQ_DATABASE_NAME not in env");
    let static_resources_dir =
        env::var("STATIC_RESOURCES_DIR").expect("STATIC_RESOURCES_DIR not in env");

    let database_url = format!("postgres://{}:{}@{}/{}", user, pass, host, name);
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&database_url)
        .await
        .expect("Could not connect to Postgres");

    let api = Router::new()
        .route("/clients", get(get_clients))
        .with_state(pool);

    // build our application with a single route
    let app = Router::new()
        .nest("/api", api)
        .fallback_service(ServeDir::new(static_resources_dir));

    // run our app with hyper, listening globally on port 3000
    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
