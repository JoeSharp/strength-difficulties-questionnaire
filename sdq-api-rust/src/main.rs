use axum::{Json, extract::State};
use axum::{Router, routing::get};
use serde::Serialize;
use sqlx::PgPool;
use sqlx::postgres::PgPoolOptions;

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
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect("postgres://sdqUser:GYUsdfsd234@localhost/sdq")
        .await
        .expect("Could not connect to Postgres");

    // build our application with a single route
    let app = Router::new()
        .route("/", get(|| async { "Hello, World!" }))
        .route("/clients", get(get_clients))
        .with_state(pool);

    // run our app with hyper, listening globally on port 3000
    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
