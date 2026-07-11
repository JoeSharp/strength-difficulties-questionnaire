use axum::http::StatusCode;
use axum::response::IntoResponse;
use axum::routing::post;
use axum::{Json, extract::State};
use axum::{Router, routing::get};
use chrono::NaiveDate;
use dotenvy::dotenv;
use sdq_model::{
    AceCounts, AceType, CareExperience, Council, DemographicField, DisabilityStatus,
    DisabilityType, EnglishAsAdditionalLanguage, Ethnicity, FundingSource, Gender, GoalType,
    Intervention, InterventionType, Posture,
};
use serde::Serialize;
use serde_json::Value;
use sqlx::postgres::PgPoolOptions;
use sqlx::{AssertSqlSafe, PgPool};
use std::collections::HashMap;
use std::env;
use std::fmt::Display;
use std::str::FromStr;
use strum::IntoEnumIterator;
use tower_http::services::ServeDir;
use tracing_subscriber::{EnvFilter, fmt};
use uuid::Uuid;

pub enum AppError {
    Db(sqlx::Error),
}

impl From<sqlx::Error> for AppError {
    fn from(e: sqlx::Error) -> Self {
        AppError::Db(e)
    }
}

impl IntoResponse for AppError {
    fn into_response(self) -> axum::response::Response {
        match self {
            AppError::Db(_) => {
                (StatusCode::INTERNAL_SERVER_ERROR, "Database error").into_response()
            }
        }
    }
}

#[derive(Debug, serde::Serialize)]
pub struct EnumValue {
    pub value: String,
    pub label: String,
}

pub fn enum_values<E>() -> Vec<EnumValue>
where
    E: IntoEnumIterator + Display + Serialize,
{
    E::iter()
        .map(|v| EnumValue {
            value: serde_json::to_string(&v)
                .unwrap()
                .trim_matches('"')
                .to_string(), // serialized form, e.g. "DAILY_GOAL"
            label: v.to_string(),
        })
        .collect()
}

#[derive(Debug, serde::Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ReferenceInfoDTO {
    pub goal_types: Vec<EnumValue>,
    pub postures: Vec<EnumValue>,
    pub demographic_fields: HashMap<DemographicField, Vec<EnumValue>>,
}

#[derive(sqlx::FromRow)]
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
#[serde(rename_all = "camelCase")]
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
    pub interventions: Vec<Intervention>,
    pub disability_types: Vec<String>,
    pub aces: AceCounts,
}

impl From<RawSdqClient> for SdqClient {
    fn from(raw: RawSdqClient) -> Self {
        let disability_types: Vec<String> = raw
            .disability_types
            .and_then(|v| serde_json::from_value(v).ok())
            .unwrap_or_default();
        let interventions: Vec<Intervention> = raw
            .interventions
            .and_then(|v| serde_json::from_value(v).ok())
            .unwrap_or_default();
        let aces: AceCounts = raw
            .aces
            .and_then(|v| serde_json::from_value(v).ok())
            .unwrap_or_default();
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
            interventions,
            disability_types,
            aces,
        }
    }
}

#[derive(Debug, serde::Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct ClientQueryDTO {
    pub partial_name: Option<String>,
    pub filters: Vec<DemographicFilter>,
}

#[derive(Debug, serde::Deserialize)]
pub struct DemographicFilter {
    pub field: DemographicField,
    pub values: Vec<String>,
}

trait ColumnName {
    fn column(&self) -> &'static str;
}

impl ColumnName for DemographicField {
    fn column(&self) -> &'static str {
        match self {
            DemographicField::Gender => "gender",
            DemographicField::Council => "council",
            DemographicField::Ethnicity => "ethnicity",
            DemographicField::EAL => "eal",
            DemographicField::DisabilityStatus => "disability_status",
            DemographicField::DisabilityType => "disability_type",
            DemographicField::CareExperience => "care_experience",
            DemographicField::ACES => "aces",
            DemographicField::FundingSource => "funding_source",
            _ => "foo", // sort this out!
        }
    }
}

pub async fn reference_info_handler() -> Json<ReferenceInfoDTO> {
    let mut demographic_fields = HashMap::new();

    demographic_fields.insert(DemographicField::Gender, enum_values::<Gender>());
    demographic_fields.insert(DemographicField::Council, enum_values::<Council>());
    demographic_fields.insert(DemographicField::Ethnicity, enum_values::<Ethnicity>());
    demographic_fields.insert(
        DemographicField::DisabilityStatus,
        enum_values::<DisabilityStatus>(),
    );
    demographic_fields.insert(
        DemographicField::EAL,
        enum_values::<EnglishAsAdditionalLanguage>(),
    );
    demographic_fields.insert(
        DemographicField::DisabilityType,
        enum_values::<DisabilityType>(),
    );
    demographic_fields.insert(
        DemographicField::CareExperience,
        enum_values::<CareExperience>(),
    );
    demographic_fields.insert(
        DemographicField::FundingSource,
        enum_values::<FundingSource>(),
    );
    demographic_fields.insert(
        DemographicField::InterventionType,
        enum_values::<InterventionType>(),
    );
    demographic_fields.insert(DemographicField::ACES, enum_values::<AceType>());

    Json(ReferenceInfoDTO {
        goal_types: enum_values::<GoalType>(),
        postures: enum_values::<Posture>(),
        demographic_fields,
    })
}

struct IncrementingIndex {
    index: usize,
}

impl IncrementingIndex {
    fn create() -> IncrementingIndex {
        IncrementingIndex { index: 0 }
    }

    fn next_index(&mut self) -> usize {
        self.index += 1;
        self.index
    }
}

pub async fn search_clients(
    pool: &PgPool,
    request: ClientQueryDTO,
) -> Result<Vec<RawSdqClient>, sqlx::Error> {
    // 1. Build SQL string completely
    let sql = {
        let mut s = String::from("SELECT * FROM client_full");
        let mut conditions = Vec::new();
        let mut placeholder = IncrementingIndex::create();

        for filter in &request.filters {
            let column = filter.field.column();

            let placeholders: Vec<String> = (0..filter.values.len())
                .map(|_| format!("${}", placeholder.next_index()))
                .collect();

            conditions.push(format!("{} IN ({})", column, placeholders.join(", ")));
        }

        if request
            .partial_name
            .as_ref()
            .filter(|s| !s.trim().is_empty())
            .is_some()
        {
            conditions.push(format!("code_name ILIKE ${}", placeholder.next_index()));
        }

        if !conditions.is_empty() {
            s.push_str(" WHERE ");
            s.push_str(&conditions.join(" AND "));
        }

        // IMPORTANT: return the final string
        s
    };

    // 2. SQL string is now FINAL — no more mutations
    let safe_sql = AssertSqlSafe(sql.as_str());
    let mut query = sqlx::query_as::<_, RawSdqClient>(safe_sql);

    // 3. Bind values
    tracing::info!("Running Query {:?}", sql);
    for filter in &request.filters {
        tracing::info!("Binding values for filter {:?}", filter);
        for value in &filter.values {
            query = query.bind(value);
        }
    }

    if let Some(name) = request
        .partial_name
        .as_ref()
        .filter(|s| !s.trim().is_empty())
    {
        tracing::info!("Binding value for partial_name {:?}", name);
        query = query.bind(format!("%{}%", name));
    }

    // 4. Execute
    query.fetch_all(pool).await.map_err(|e| {
        tracing::error!("Search query failed: {:?}", e);
        e
    })
}

pub async fn search_clients_handler(
    State(pool): State<PgPool>,
    Json(query): Json<ClientQueryDTO>,
) -> Result<Json<Vec<SdqClient>>, AppError> {
    let raw = search_clients(&pool, query).await?;
    let typed = raw.into_iter().map(SdqClient::from).collect();
    Ok(Json(typed))
}

async fn get_clients(State(pool): State<PgPool>) -> Json<Vec<SdqClient>> {
    let clients: Vec<SdqClient> = sqlx::query_as::<_, RawSdqClient>(
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
    "#,
    )
    .fetch_all(&pool)
    .await
    .map_err(|e| {
        tracing::error!("Search query failed: {:?}", e);
        e
    })
    .unwrap()
    .into_iter()
    .map(SdqClient::from)
    .collect();
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
    fmt().with_env_filter(EnvFilter::new("info")).init();
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

    let client_api = Router::new()
        .route("/", get(get_clients))
        .route("/search", post(search_clients_handler));

    let reference_api = Router::new().route("/", get(reference_info_handler));

    let api = Router::new()
        .nest("/reference", reference_api)
        .nest("/client", client_api)
        .with_state(pool);

    // build our application with a single route
    let app = Router::new()
        .nest("/api", api)
        .fallback_service(ServeDir::new(static_resources_dir));

    // run our app with hyper, listening globally on port 3000
    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}
