use axum::Router;
use dotenvy::dotenv;
use sdq_api::build_api::{AppState, build_api};
use sdq_db::client_service::ClientServiceSqlxImpl;
use sqlx::postgres::PgPoolOptions;
use std::env;
use std::sync::Arc;
use tower_http::services::ServeDir;
use tracing_subscriber::{EnvFilter, fmt};

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

struct DbConfig {
    pub username: String,
    pub password: String,
    pub host: String,
    pub name: String,
}

fn read_db_config_from_env() -> DbConfig {
    let username = env::var("SDQ_DATABASE_USERNAME").expect("SDQ_DATABASE_USERNAME not in env");
    let password = env::var("SDQ_DATABASE_PASSWORD").expect("SDQ_DATABASE_PASSWORD not in env");
    let host = env::var("SDQ_DATABASE_HOST").expect("SDQ_DATABASE_HOST not in env");
    let name = env::var("SDQ_DATABASE_NAME").expect("SDQ_DATABASE_NAME not in env");

    DbConfig {
        username,
        password,
        host,
        name,
    }
}

fn get_static_resource_service() -> ServeDir {
    let static_resources_dir =
        env::var("STATIC_RESOURCES_DIR").expect("STATIC_RESOURCES_DIR not in env");
    ServeDir::new(static_resources_dir)
}

#[tokio::main]
async fn main() {
    fmt().with_env_filter(EnvFilter::new("info")).init();
    dotenv().ok(); // load .env

    println!("{}", &SPLASH);

    let db_config = read_db_config_from_env();
    let database_url = format!(
        "postgres://{}:{}@{}/{}",
        db_config.username, db_config.password, db_config.host, db_config.name
    );
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&database_url)
        .await
        .expect("Could not connect to Postgres");

    let state = AppState {
        client_service: Arc::new(ClientServiceSqlxImpl::new(pool)),
    };

    let api = build_api(state);

    // build our application with a single route
    let app = Router::new()
        .nest("/api", api)
        .fallback_service(get_static_resource_service());

    // run our app with hyper, listening globally on port 3000
    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
