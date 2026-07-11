use axum::http::StatusCode;
use axum::response::IntoResponse;
use axum::routing::post;
use axum::{Json, extract::State};
use axum::{Router, routing::get};
use chrono::NaiveDate;
use dotenvy::dotenv;
use serde::{Deserialize, Serialize};
use serde_json::Value;
use sqlx::postgres::PgPoolOptions;
use sqlx::{AssertSqlSafe, PgPool};
use std::collections::HashMap;
use std::env;
use std::fmt::Display;
use std::str::FromStr;
use strum::{Display, EnumIter, EnumString, IntoEnumIterator};
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

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum GoalType {
    #[strum(serialize = "EMOTIONAL", to_string = "Emotional")]
    Emotional,
    #[strum(serialize = "BEHAVIOURAL", to_string = "Behavioural")]
    Behavioural,
    #[strum(serialize = "RELATIONAL", to_string = "Relational")]
    Relational,
    #[strum(serialize = "REGULATORY_CAPACITY", to_string = "Regulatory Capacity")]
    RegulatoryCapacity,
    #[strum(serialize = "TRAUMA_RECOVERY", to_string = "Trauma Recovery")]
    TraumaRecovery,
    #[strum(
        serialize = "SELF_ESTEEM_CONFIDENCE",
        to_string = "Self Esteem/Confidence"
    )]
    SelfEsteemConfidence,
    #[strum(serialize = "UNKNOWN", to_string = "Unknown")]
    Unknown,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
pub enum Posture {
    Internalising,
    Externalising,
    ProSocial,
}

#[derive(Debug, serde::Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ReferenceInfoDTO {
    pub goal_types: Vec<EnumValue>,
    pub postures: Vec<EnumValue>,
    pub demographic_fields: HashMap<DemographicField, Vec<EnumValue>>,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Gender {
    #[strum(serialize = "MALE", to_string = "Male")]
    Male,

    #[strum(serialize = "FEMALE", to_string = "Female")]
    Female,

    #[strum(serialize = "NON_BINARY", to_string = "Non-Binary")]
    NonBinary,

    #[strum(serialize = "OTHER", to_string = "Other")]
    Other,

    #[strum(serialize = "PREFER_NOT_TO_SAY", to_string = "Prefer Not To Say")]
    PreferNotToSay,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Council {
    #[strum(serialize = "CHELTENHAM", to_string = "Cheltenham")]
    Cheltenham,
    #[strum(serialize = "GLOUCESTER_CITY", to_string = "Gloucester City")]
    GloucesterCity,
    #[strum(serialize = "STROUD", to_string = "Stroud")]
    Stroud,
    #[strum(serialize = "TEWKESBURY", to_string = "Tewksbury")]
    Tewksbury,
    #[strum(serialize = "FOREST_OF_DEAN", to_string = "Forest of Dean")]
    ForestOfDean,
    #[strum(serialize = "NORTH_COTSWOLDS", to_string = "North Cotswolds")]
    NorthCotswolds,
    #[strum(serialize = "OUT_OF_COUNTY", to_string = "Out of County")]
    OutOfCounty,
    #[strum(serialize = "UNKNOWN", to_string = "Unknown")]
    Unknown,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Ethnicity {
    #[strum(serialize = "WHITE_BRITISH", to_string = "White British")]
    WhiteBritish,
    #[strum(serialize = "WHITE_EUROPEAN", to_string = "White European")]
    WhiteEuropean,
    #[strum(serialize = "MIXED", to_string = "Mixed")]
    Mixed,
    #[strum(serialize = "ASIAN", to_string = "Asian/Asian British")]
    Asian,
    #[strum(
        serialize = "BLACK",
        to_string = "Black/African/Caribbean/Black British"
    )]
    Black,
    #[strum(serialize = "TRAVELLER", to_string = "Traveller")]
    Traveller,
    #[strum(serialize = "OTHER", to_string = "Other")]
    Other,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum DisabilityStatus {
    #[strum(serialize = "DISABILITY", to_string = "Disability")]
    Disability,
    #[strum(serialize = "NO_DISABILITY", to_string = "No Disability")]
    NoDisability,
    #[strum(serialize = "PREFER_NOT_TO_SAY", to_string = "Prefer Not To Say")]
    PreferNotToSay,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum DisabilityType {
    #[strum(serialize = "PHYSICAL", to_string = "Physical")]
    Physical,
    #[strum(
        serialize = "SENSORY",
        to_string = "Sensory Impairment (e.g. hearing or visual)"
    )]
    Sensory,
    #[strum(serialize = "LEARNING", to_string = "Learning")]
    Learning,
    #[strum(
        serialize = "NEURODIVERSE",
        to_string = "Neurodivergence (e.g. ASD or ADHD)"
    )]
    Neurodiverse,
    #[strum(
        serialize = "MENTAL_HEALTH_CONDITION",
        to_string = "Mental Health Condition"
    )]
    MentalHealthCondition,
    #[strum(serialize = "CHRONIC", to_string = "Long Term or Chronic Illness")]
    Chronic,
    #[strum(
        serialize = "SPEECH_OR_COMMUNICATION",
        to_string = "Speech or Communication"
    )]
    SpeechOrCommunication,
    #[strum(
        serialize = "COGNITIVE_OR_MEMORY",
        to_string = "Cognitive or Memory Impairment"
    )]
    CognitiveOrMemory,
    #[strum(serialize = "OTHER", to_string = "Other")]
    Other,
    #[strum(serialize = "NOT_APPLICABLE", to_string = "N/A")]
    NotApplicable,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum CareExperience {
    #[strum(serialize = "NO", to_string = "No")]
    No,
    #[strum(serialize = "YES_ADOPTED", to_string = "Yes - Adopted")]
    YesAdopted,
    #[strum(serialize = "YES_CHILD_IN_CARE", to_string = "Yes - Child in Care")]
    YesChildInCare,
    SGO,
    #[strum(serialize = "KINSHIP", to_string = "Kinship")]
    Kinship,
    #[strum(serialize = "UNKNOWN", to_string = "Unknown")]
    Unknown,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum FundingSource {
    EHCP,
    PEP,
    ASGSF,
    #[strum(serialize = "PRIVATE", to_string = "Private")]
    Private,
    OtherCharitable,
    #[strum(
        serialize = "SUBSIDISED_SESSION_FUND",
        to_string = "Subsidised Session Fund"
    )]
    SubsidisedSessionFund,
    #[strum(serialize = "PROJECT", to_string = "Project")]
    Project,
    #[strum(serialize = "UNKNOWN", to_string = "Unknown")]
    Unknown,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum InterventionType {
    CCPT,
    CPRT,
    PTP,
    IA,
    #[strum(serialize = "UNKNOWN", to_string = "Unknown")]
    Unknown,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum AceType {
    #[strum(serialize = "COMMUNITY", to_string = "Community")]
    Community,
    #[strum(serialize = "SOCIO_ECONOMIC", to_string = "Socio-economic")]
    SocioEconomic,
    #[strum(
        serialize = "DISCRIMINATION",
        to_string = "Discrimination & Social Exclusion"
    )]
    Discrimination,
    #[strum(serialize = "HEALTH", to_string = "Health")]
    Health,
    #[strum(serialize = "EDUCATION", to_string = "Education")]
    Education,
    #[strum(serialize = "BEREAVEMENT", to_string = "Bereavement & Loss")]
    Bereavement,
    #[strum(serialize = "DIGITAL_ONLINE", to_string = "Digital/Online Adversities")]
    DigitalOnline,
    #[strum(serialize = "ENVIRONMENTAL", to_string = "Environment Adversities")]
    Environmental,
    #[strum(
        serialize = "CHILD_WELFARE",
        to_string = "Child Welfare or Statutory Intervention Experiences"
    )]
    ChildWelfare,
    #[strum(serialize = "GENERIC", to_string = "Generic")]
    Generic,
}

#[derive(Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum EnglishAsAdditionalLanguage {
    #[strum(serialize = "YES", to_string = "Yes")]
    Yes,
    #[strum(serialize = "NO", to_string = "No")]
    No,
    #[strum(serialize = "PREFER_NOT_TO_SAY", to_string = "Prefer Not To Say")]
    PreferNotToSay,
}

#[derive(Debug, serde::Deserialize, serde::Serialize)]
pub struct Intervention {
    pub r#type: String,
    pub sessions: i32,
}

#[derive(Debug, serde::Deserialize, serde::Serialize)]
pub struct AceCounts(pub HashMap<String, i32>);

impl Default for AceCounts {
    fn default() -> AceCounts {
        AceCounts(HashMap::new())
    }
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

#[derive(
    Eq, Hash, PartialEq, Debug, Clone, Serialize, Deserialize, EnumIter, EnumString, Display,
)]
pub enum DemographicField {
    Gender,
    Council,
    Ethnicity,
    EAL,
    #[strum(serialize = "DisabilityStatus", to_string = "Disability Status")]
    DisabilityStatus,
    #[strum(serialize = "DisabilityType", to_string = "Disability Type")]
    DisabilityType,
    #[strum(serialize = "CareExperience", to_string = "Care Experience")]
    CareExperience,
    ACES,
    #[strum(serialize = "InterventionType", to_string = "Intervention Type")]
    InterventionType,
    #[strum(serialize = "FundingSource", to_string = "Funding Source")]
    FundingSource,
}

impl DemographicField {
    pub fn column(&self) -> &'static str {
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
