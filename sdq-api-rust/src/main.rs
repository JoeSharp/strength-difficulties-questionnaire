use axum::{Json, extract::State};
use axum::{Router, routing::get};
use chrono::NaiveDate;
use dotenvy::dotenv;
use serde::Serialize;
use serde_json::Value;
use sqlx::PgPool;
use sqlx::postgres::PgPoolOptions;
use std::collections::HashMap;
use std::env;
use std::str::FromStr;
use tower_http::services::ServeDir;
use uuid::Uuid;

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Gender {
    Male,
    Female,
    NonBinary,
    Other,
    PreferNotToSay,
}

impl Gender {
    pub fn display(&self) -> &'static str {
        match self {
            Gender::Male => "Male",
            Gender::Female => "Female",
            Gender::NonBinary => "Non-Binary",
            Gender::Other => "Other",
            Gender::PreferNotToSay => "Prefer Not To Say",
        }
    }
}

impl FromStr for Gender {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "MALE" => Ok(Gender::Male),
            "FEMALE" => Ok(Gender::Female),
            "NON_BINARY" => Ok(Gender::NonBinary),
            "OTHER" => Ok(Gender::Other),
            _ => Ok(Gender::PreferNotToSay),
        }
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Council {
    Cheltenham,
    GloucesterCity,
    Stroud,
    Tewksbury,
    ForestOfDean,
    NorthCotswolds,
    OutOfCounty,
    Unknown,
}

impl Council {
    pub fn display(&self) -> &'static str {
        match self {
            Council::Cheltenham => "Cheltenham",
            Council::GloucesterCity => "Gloucester City",
            Council::Stroud => "Stroud",
            Council::Tewksbury => "Tewksbury",
            Council::ForestOfDean => "Forest of Dean",
            Council::NorthCotswolds => "North Cotswolds",
            Council::OutOfCounty => "Out of County",
            Council::Unknown => "Unknown",
        }
    }
}

impl FromStr for Council {
    type Err = ();
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "CHELTENHAM" => Ok(Council::Cheltenham),
            "GLOUCESTER_CITY" => Ok(Council::GloucesterCity),
            "STROUD" => Ok(Council::Stroud),
            "TEWKESBURY" => Ok(Council::Tewksbury),
            "FOREST_OF_DEAN" => Ok(Council::ForestOfDean),
            "NORTH_COTSWOLDS" => Ok(Council::NorthCotswolds),
            "OUT_OF_COUNTY" => Ok(Council::OutOfCounty),
            _ => Ok(Council::Unknown),
        }
    }
}

#[derive(Debug, serde::Deserialize, serde::Serialize)]
pub struct Intervention {
    pub r#type: String,
    pub sessions: i32,
}

#[derive(Debug, serde::Deserialize, serde::Serialize)]
pub struct AceCounts(pub HashMap<String, i32>);

pub struct RawSdqClient {
    pub client_id: Option<Uuid>,
    pub code_name: Option<String>,
    pub date_of_birth: Option<NaiveDate>,
    pub gender: Option<String>,
    pub council: Option<String>,
    pub ethnicity: Option<String>,
    pub eal: Option<String>,
    pub disability_status: Option<String>,
    pub care_experience: Option<String>,
    pub funding_source: Option<String>,
    pub interventions: Option<Value>,
    pub disability_types: Option<Value>,
    pub aces: Option<Value>,
}

#[derive(Serialize)]
pub struct SdqClient {
    pub client_id: Option<Uuid>,
    pub code_name: Option<String>,
    pub date_of_birth: Option<NaiveDate>,
    pub gender: Option<Gender>,
    pub council: Option<Council>,
    pub ethnicity: Option<String>,
    pub eal: Option<String>,
    pub disability_status: Option<String>,
    pub care_experience: Option<String>,
    pub funding_source: Option<String>,
    pub interventions: Option<Vec<Intervention>>,
    pub disability_types: Option<Vec<String>>,
    pub aces: Option<AceCounts>,
}

impl From<RawSdqClient> for SdqClient {
    fn from(raw: RawSdqClient) -> Self {
        SdqClient {
            client_id: raw.client_id,
            code_name: raw.code_name,
            date_of_birth: raw.date_of_birth,
            gender: raw.gender.and_then(|g| Gender::from_str(&g).ok()),
            council: raw.council.and_then(|g| Council::from_str(&g).ok()),
            ethnicity: raw.ethnicity,
            eal: raw.eal,
            disability_status: raw.disability_status,
            care_experience: raw.care_experience,
            funding_source: raw.funding_source,

            interventions: raw
                .interventions
                .and_then(|v| serde_json::from_value(v).ok()),

            disability_types: raw
                .disability_types
                .and_then(|v| serde_json::from_value(v).ok()),

            aces: raw.aces.and_then(|v| serde_json::from_value(v).ok()),
        }
    }
}

async fn get_clients(State(pool): State<PgPool>) -> Json<Vec<SdqClient>> {
    let raw_clients = sqlx::query_as!(
        RawSdqClient,
        r#"
    SELECT
        client_id,
        code_name,
        date_of_birth,
        gender,
        council,
        ethnicity,
        eal,
        disability_status,
        care_experience,
        funding_source,
        interventions,
        disability_types,
        aces
    FROM client_full
    "#
    )
    .fetch_all(&pool)
    .await
    .unwrap();

    let clients: Vec<SdqClient> = raw_clients.into_iter().map(SdqClient::from).collect();
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
