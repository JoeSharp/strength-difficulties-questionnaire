use axum::{Json, extract::State};
use axum::{Router, routing::get};
use chrono::NaiveDate;
use dotenvy::dotenv;
use serde::Serialize;
use sqlx::PgPool;
use sqlx::postgres::PgPoolOptions;
use std::env;
use tower_http::services::ServeDir;
use uuid::Uuid;

#[derive(Serialize)]
pub struct SdqClient {
    pub client_id: Option<Uuid>,
    pub code_name: Option<String>,
}

async fn get_clients(State(pool): State<PgPool>) -> Json<Vec<SdqClient>> {
    let clients = sqlx::query_as!(
        SdqClient,
        r#"
    SELECT
        client_id,
        code_name
    FROM client_full
    "#
    )
    .fetch_all(&pool)
    .await
    .unwrap();

    Json(clients)
}

const SPLASH: &str = r#"
  _______/  |________   ____   ____    _____/  |_|  |__                              
 /  ___/\   __\_  __ \_/ __ \ /    \  / ___\   __\  |  \                             
 \___ \  |  |  |  | \/\  ___/|   |  \/ /_/  >  | |   Y  \                            
/____  > |__|  |__|    \___  >___|  /\___  /|__| |___|  /                            
     \/                    \/     \//_____/           \/                             
    .___.__  _____  _____.__             .__   __  .__                               
  __| _/|__|/ ____\/ ____\__| ____  __ __|  |_/  |_|__| ____   ______                
 / __ | |  \   __\\   __\|  |/ ___\|  |  \  |\   __\  |/ __ \ /  ___/                
/ /_/ | |  ||  |   |  |  |  \  \___|  |  /  |_|  | |  \  ___/ \___ \                 
\____ | |__||__|   |__|  |__|\___  >____/|____/__| |__|\___  >____  >                
     \/                          \/                        \/     \/                 
                               __  .__                            .__                
  ________ __   ____   _______/  |_|__| ____   ____   ____ _____  |__|______   ____  
 / ____/  |  \_/ __ \ /  ___/\   __\  |/  _ \ /    \ /    \\__  \ |  \_  __ \_/ __ \ 
< <_|  |  |  /\  ___/ \___ \  |  | |  (  <_> )   |  \   |  \/ __ \|  ||  | \/\  ___/ 
 \__   |____/  \___  >____  > |__| |__|\____/|___|  /___|  (____  /__||__|    \___  >
    |__|           \/     \/                      \/     \/     \/                \/ 
"#;

#[tokio::main]
async fn main() {
    dotenv().ok(); // load .env

    println!("{}", &SPLASH);

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
        .route("/client", get(get_clients))
        .with_state(pool);

    // build our application with a single route
    let app = Router::new()
        .nest("/api", api)
        .fallback_service(ServeDir::new(static_resources_dir));

    // run our app with hyper, listening globally on port 3000
    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
