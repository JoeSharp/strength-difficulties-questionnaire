use axum::http::StatusCode;
use axum::response::IntoResponse;
use axum::routing::post;
use axum::{Json, extract::State};
use axum::{Router, routing::get};
use chrono::NaiveDate;
use dotenvy::dotenv;
use serde::Serialize;
use serde_json::Value;
use sqlx::postgres::PgPoolOptions;
use sqlx::{AssertSqlSafe, PgPool};
use std::collections::HashMap;
use std::env;
use std::str::FromStr;
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
    pub label: &'static str,
}

pub trait EnumValues {
    fn values() -> Vec<EnumValue>;
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum GoalType {
    Emotional,
    Behavioural,
    Relational,
    RegulatoryCapacity,
    TraumaRecovery,
    SelfEsteemConfidence,
    Unknown,
}

impl GoalType {
    pub fn label(&self) -> &'static str {
        match self {
            GoalType::Emotional => "Emotional",
            GoalType::Behavioural => "Behavioural",
            GoalType::Relational => "Relational",
            GoalType::RegulatoryCapacity => "Regulatory Capacity",
            GoalType::TraumaRecovery => "Trauma Recovery",
            GoalType::SelfEsteemConfidence => "Self Esteem/Confidence",
            GoalType::Unknown => "Unknown",
        }
    }
}

impl EnumValues for GoalType {
    fn values() -> Vec<EnumValue> {
        use GoalType::*;
        vec![
            Emotional,
            Behavioural,
            Relational,
            RegulatoryCapacity,
            TraumaRecovery,
            SelfEsteemConfidence,
            Unknown,
        ]
        .into_iter()
        .map(|v| EnumValue {
            value: serde_json::to_string(&v)
                .unwrap()
                .trim_matches('"')
                .to_string(),
            label: v.label(),
        })
        .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
pub enum Posture {
    Internalising,
    Externalising,
    ProSocial,
}

impl EnumValues for Posture {
    fn values() -> Vec<EnumValue> {
        use Posture::*;
        vec![Internalising, Externalising, ProSocial]
            .into_iter()
            .map(|v| EnumValue {
                value: serde_json::to_string(&v)
                    .unwrap()
                    .trim_matches('"')
                    .to_string(),
                label: match v {
                    Internalising => "Internalising",
                    Externalising => "Externalising",
                    ProSocial => "Pro-Social",
                },
            })
            .collect()
    }
}

#[derive(Debug, serde::Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ReferenceInfoDTO {
    pub goal_types: Vec<EnumValue>,
    pub postures: Vec<EnumValue>,
    pub demographic_fields: HashMap<DemographicField, Vec<EnumValue>>,
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Gender {
    Male,
    Female,
    NonBinary,
    Other,
    PreferNotToSay,
}

impl EnumValues for Gender {
    fn values() -> Vec<EnumValue> {
        use Gender::*;
        vec![Male, Female, NonBinary, Other, PreferNotToSay]
            .into_iter()
            .map(|v| EnumValue {
                value: serde_json::to_string(&v)
                    .unwrap()
                    .trim_matches('"')
                    .to_string(),
                label: v.display(),
            })
            .collect()
    }
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

impl EnumValues for Council {
    fn values() -> Vec<EnumValue> {
        use Council::*;
        vec![
            Cheltenham,
            GloucesterCity,
            Stroud,
            Tewksbury,
            ForestOfDean,
            NorthCotswolds,
            OutOfCounty,
        ]
        .into_iter()
        .map(|v| EnumValue {
            value: serde_json::to_string(&v)
                .unwrap()
                .trim_matches('"')
                .to_string(),
            label: v.display(),
        })
        .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum Ethnicity {
    WhiteBritish,
    WhiteEuropean,
    Mixed,
    Asian,
    Black,
    Traveller,
    Other,
}

impl Ethnicity {
    pub fn display(&self) -> &'static str {
        match self {
            Ethnicity::WhiteBritish => "White British",
            Ethnicity::WhiteEuropean => "White European",
            Ethnicity::Mixed => "Mixed",
            Ethnicity::Asian => "Asian/Asian British",
            Ethnicity::Black => "Black/African/Caribbean/Black British",
            Ethnicity::Traveller => "Traveller",
            Ethnicity::Other => "Other",
        }
    }
}

impl FromStr for Ethnicity {
    type Err = ();
    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "WHITE_BRITISH" => Ok(Ethnicity::WhiteBritish),
            "WHITE_EUROPEAN" => Ok(Ethnicity::WhiteEuropean),
            "MIXED" => Ok(Ethnicity::Mixed),
            "ASIAN" => Ok(Ethnicity::Asian),
            "BLACK" => Ok(Ethnicity::Black),
            "TRAVELLER" => Ok(Ethnicity::Traveller),
            _ => Ok(Ethnicity::Other),
        }
    }
}

impl EnumValues for Ethnicity {
    fn values() -> Vec<EnumValue> {
        use Ethnicity::*;
        vec![
            WhiteBritish,
            WhiteEuropean,
            Mixed,
            Asian,
            Black,
            Traveller,
            Other,
        ]
        .into_iter()
        .map(|v| EnumValue {
            value: serde_json::to_string(&v)
                .unwrap()
                .trim_matches('"')
                .to_string(),
            label: v.display(),
        })
        .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum DisabilityStatus {
    Disability,
    NoDisability,
    PreferNotToSay,
}
impl DisabilityStatus {
    pub fn display(&self) -> &'static str {
        match self {
            DisabilityStatus::Disability => "Disability",
            DisabilityStatus::NoDisability => "No Disability",
            DisabilityStatus::PreferNotToSay => "Prefer Not To Say",
        }
    }
}
impl std::str::FromStr for DisabilityStatus {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "DISABILITY" => Ok(DisabilityStatus::Disability),
            "NO_DISABILITY" => Ok(DisabilityStatus::NoDisability),
            "PREFER_NOT_TO_SAY" => Ok(DisabilityStatus::PreferNotToSay),
            _ => Err(()),
        }
    }
}
impl EnumValues for DisabilityStatus {
    fn values() -> Vec<EnumValue> {
        use DisabilityStatus::*;
        vec![Disability, NoDisability, PreferNotToSay]
            .into_iter()
            .map(|v| {
                let value = serde_json::to_string(&v).unwrap();
                let value = value.trim_matches('"').to_string();

                EnumValue {
                    value,
                    label: v.display(),
                }
            })
            .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum DisabilityType {
    Physical,
    Sensory,
    Learning,
    Neurodiverse,
    MentalHealthCondition,
    Chronic,
    SpeechOrCommunication,
    CognitiveOrMemory,
    Other,
    NotApplicable,
}

impl DisabilityType {
    pub fn display(&self) -> &'static str {
        match self {
            DisabilityType::Physical => "Physical",
            DisabilityType::Sensory => "Sensory Impairment (e.g. hearing or visual)",
            DisabilityType::Learning => "Learning",
            DisabilityType::Neurodiverse => "Neurodivergence (e.g. ASD or ADHD)",
            DisabilityType::MentalHealthCondition => "Mental Health Condition",
            DisabilityType::Chronic => "Long Term or Chronic Illness",
            DisabilityType::SpeechOrCommunication => "Speech or Communication",
            DisabilityType::CognitiveOrMemory => "Cognitive or Memory Impairment",
            DisabilityType::Other => "Other",
            DisabilityType::NotApplicable => "N/A",
        }
    }
}

impl std::str::FromStr for DisabilityType {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "PHYSICAL" => Ok(DisabilityType::Physical),
            "SENSORY" => Ok(DisabilityType::Sensory),
            "LEARNING" => Ok(DisabilityType::Learning),
            "NEURODIVERSE" => Ok(DisabilityType::Neurodiverse),
            "MENTAL_HEALTH_CONDITION" => Ok(DisabilityType::MentalHealthCondition),
            "CHRONIC" => Ok(DisabilityType::Chronic),
            "SPEECH_OR_COMMUNICATION" => Ok(DisabilityType::SpeechOrCommunication),
            "COGNITIVE_OR_MEMORY" => Ok(DisabilityType::CognitiveOrMemory),
            "OTHER" => Ok(DisabilityType::Other),
            "NOT_APPLICABLE" => Ok(DisabilityType::NotApplicable),
            _ => Err(()),
        }
    }
}

impl EnumValues for DisabilityType {
    fn values() -> Vec<EnumValue> {
        use DisabilityType::*;
        vec![
            Physical,
            Sensory,
            Learning,
            Neurodiverse,
            MentalHealthCondition,
            Chronic,
            SpeechOrCommunication,
            CognitiveOrMemory,
            Other,
            NotApplicable,
        ]
        .into_iter()
        .map(|v| {
            let value = serde_json::to_string(&v).unwrap();
            let value = value.trim_matches('"').to_string();

            EnumValue {
                value,
                label: v.display(),
            }
        })
        .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum CareExperience {
    No,
    YesAdopted,
    YesChildInCare,
    Sgo,
    Kinship,
    Unknown,
}

impl CareExperience {
    pub fn display(&self) -> &'static str {
        match self {
            CareExperience::No => "No",
            CareExperience::YesAdopted => "Yes - Adopted",
            CareExperience::YesChildInCare => "Yes - Child in Care",
            CareExperience::Sgo => "SGO",
            CareExperience::Kinship => "Kinship",
            CareExperience::Unknown => "Unknown",
        }
    }
}
impl std::str::FromStr for CareExperience {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "NO" => Ok(CareExperience::No),
            "YES_ADOPTED" => Ok(CareExperience::YesAdopted),
            "YES_CHILD_IN_CARE" => Ok(CareExperience::YesChildInCare),
            "SGO" => Ok(CareExperience::Sgo),
            "KINSHIP" => Ok(CareExperience::Kinship),
            "UNKNOWN" => Ok(CareExperience::Unknown),
            _ => Err(()),
        }
    }
}
impl EnumValues for CareExperience {
    fn values() -> Vec<EnumValue> {
        use CareExperience::*;
        vec![No, YesAdopted, YesChildInCare, Sgo, Kinship, Unknown]
            .into_iter()
            .map(|v| {
                let value = serde_json::to_string(&v).unwrap();
                let value = value.trim_matches('"').to_string();

                EnumValue {
                    value,
                    label: v.display(),
                }
            })
            .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum FundingSource {
    Ehcp,
    Pep,
    Asgsf,
    Private,
    OtherCharitable,
    SubsidisedSessionFund,
    Project,
    Unknown,
}
impl FundingSource {
    pub fn display(&self) -> &'static str {
        match self {
            FundingSource::Ehcp => "EHCP",
            FundingSource::Pep => "PEP",
            FundingSource::Asgsf => "ASGSF",
            FundingSource::Private => "Private",
            FundingSource::OtherCharitable => "Other Charitable",
            FundingSource::SubsidisedSessionFund => "Subsidised Session Fund",
            FundingSource::Project => "Project",
            FundingSource::Unknown => "Unknown",
        }
    }
}
impl std::str::FromStr for FundingSource {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "EHCP" => Ok(FundingSource::Ehcp),
            "PEP" => Ok(FundingSource::Pep),
            "ASGSF" => Ok(FundingSource::Asgsf),
            "PRIVATE" => Ok(FundingSource::Private),
            "OTHER_CHARITABLE" => Ok(FundingSource::OtherCharitable),
            "SUBSIDISED_SESSION_FUND" => Ok(FundingSource::SubsidisedSessionFund),
            "PROJECT" => Ok(FundingSource::Project),
            "UNKNOWN" => Ok(FundingSource::Unknown),
            _ => Err(()),
        }
    }
}
impl EnumValues for FundingSource {
    fn values() -> Vec<EnumValue> {
        use FundingSource::*;
        vec![
            Ehcp,
            Pep,
            Asgsf,
            Private,
            OtherCharitable,
            SubsidisedSessionFund,
            Project,
            Unknown,
        ]
        .into_iter()
        .map(|v| {
            let value = serde_json::to_string(&v).unwrap();
            let value = value.trim_matches('"').to_string();

            EnumValue {
                value,
                label: v.display(),
            }
        })
        .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum InterventionType {
    Ccpt,
    Cprt,
    Ptp,
    Ia,
    Unknown,
}
impl InterventionType {
    pub fn display(&self) -> &'static str {
        match self {
            InterventionType::Ccpt => "CCPT",
            InterventionType::Cprt => "CPRT",
            InterventionType::Ptp => "PTP",
            InterventionType::Ia => "IA",
            InterventionType::Unknown => "UKKNOWN",
        }
    }
}
impl std::str::FromStr for InterventionType {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "CCPT" => Ok(InterventionType::Ccpt),
            "CPRT" => Ok(InterventionType::Cprt),
            "PTP" => Ok(InterventionType::Ptp),
            "IA" => Ok(InterventionType::Ia),
            "UKKNOWN" => Ok(InterventionType::Unknown),
            _ => Err(()),
        }
    }
}
impl EnumValues for InterventionType {
    fn values() -> Vec<EnumValue> {
        use InterventionType::*;
        vec![Ccpt, Cprt, Ptp, Ia, Unknown]
            .into_iter()
            .map(|v| {
                let value = serde_json::to_string(&v).unwrap();
                let value = value.trim_matches('"').to_string();

                EnumValue {
                    value,
                    label: v.display(),
                }
            })
            .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum AceType {
    Community,
    SocioEconomic,
    Discrimination,
    Health,
    Education,
    Bereavement,
    DigitalOnline,
    Environmental,
    ChildWelfare,
    Generic,
}

impl AceType {
    pub fn display(&self) -> &'static str {
        match self {
            AceType::Community => "Community",
            AceType::SocioEconomic => "Socio-economic",
            AceType::Discrimination => "Discrimination & Social Exclusion",
            AceType::Health => "Health",
            AceType::Education => "Education",
            AceType::Bereavement => "Bereavement & Loss",
            AceType::DigitalOnline => "Digital/Online Adversities",
            AceType::Environmental => "Environment Adversities",
            AceType::ChildWelfare => "Child Welfare or Statutory Intervention Experiences",
            AceType::Generic => "Generic",
        }
    }
}
impl std::str::FromStr for AceType {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "COMMUNITY" => Ok(AceType::Community),
            "SOCIO_ECONOMIC" => Ok(AceType::SocioEconomic),
            "DISCRIMINATION" => Ok(AceType::Discrimination),
            "HEALTH" => Ok(AceType::Health),
            "EDUCATION" => Ok(AceType::Education),
            "BEREAVEMENT" => Ok(AceType::Bereavement),
            "DIGITAL_ONLINE" => Ok(AceType::DigitalOnline),
            "ENVIRONMENTAL" => Ok(AceType::Environmental),
            "CHILD_WELFARE" => Ok(AceType::ChildWelfare),
            "GENERIC" => Ok(AceType::Generic),
            _ => Err(()),
        }
    }
}
impl EnumValues for AceType {
    fn values() -> Vec<EnumValue> {
        use AceType::*;
        vec![
            Community,
            SocioEconomic,
            Discrimination,
            Health,
            Education,
            Bereavement,
            DigitalOnline,
            Environmental,
            ChildWelfare,
            Generic,
        ]
        .into_iter()
        .map(|v| {
            let value = serde_json::to_string(&v).unwrap();
            let value = value.trim_matches('"').to_string();

            EnumValue {
                value,
                label: v.display(),
            }
        })
        .collect()
    }
}

#[derive(Debug, Clone, serde::Serialize, serde::Deserialize)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum EnglishAsAdditionalLanguage {
    Yes,
    No,
    PreferNotToSay,
}
impl EnglishAsAdditionalLanguage {
    pub fn display(&self) -> &'static str {
        match self {
            EnglishAsAdditionalLanguage::Yes => "Yes",
            EnglishAsAdditionalLanguage::No => "No",
            EnglishAsAdditionalLanguage::PreferNotToSay => "Prefer Not To Say",
        }
    }
}
impl std::str::FromStr for EnglishAsAdditionalLanguage {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "YES" => Ok(EnglishAsAdditionalLanguage::Yes),
            "NO" => Ok(EnglishAsAdditionalLanguage::No),
            "PREFER_NOT_TO_SAY" => Ok(EnglishAsAdditionalLanguage::PreferNotToSay),
            _ => Err(()),
        }
    }
}
impl EnumValues for EnglishAsAdditionalLanguage {
    fn values() -> Vec<EnumValue> {
        use EnglishAsAdditionalLanguage::*;
        vec![Yes, No, PreferNotToSay]
            .into_iter()
            .map(|v| {
                let value = serde_json::to_string(&v).unwrap();
                let value = value.trim_matches('"').to_string();

                EnumValue {
                    value,
                    label: v.display(),
                }
            })
            .collect()
    }
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

#[derive(Eq, PartialEq, Hash, Debug, serde::Serialize, serde::Deserialize)]
pub enum DemographicField {
    Gender,
    Council,
    Ethnicity,
    EAL,
    DisabilityStatus,
    DisabilityType,
    CareExperience,
    ACES,
    InterventionType,
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

    demographic_fields.insert(DemographicField::Gender, Gender::values());
    demographic_fields.insert(DemographicField::Council, Council::values());
    demographic_fields.insert(DemographicField::Ethnicity, Ethnicity::values());
    demographic_fields.insert(
        DemographicField::DisabilityStatus,
        DisabilityStatus::values(),
    );
    demographic_fields.insert(DemographicField::EAL, EnglishAsAdditionalLanguage::values());
    demographic_fields.insert(DemographicField::DisabilityType, DisabilityType::values());
    demographic_fields.insert(DemographicField::CareExperience, CareExperience::values());
    demographic_fields.insert(DemographicField::FundingSource, FundingSource::values());
    demographic_fields.insert(
        DemographicField::InterventionType,
        InterventionType::values(),
    );
    demographic_fields.insert(DemographicField::ACES, AceType::values());

    Json(ReferenceInfoDTO {
        goal_types: GoalType::values(),
        postures: Posture::values(),
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

        if request.partial_name.is_some() {
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
    for filter in &request.filters {
        for value in &filter.values {
            query = query.bind(value);
        }
    }

    if let Some(name) = request.partial_name {
        query = query.bind(format!("%{}%", name));
    }

    tracing::info!("Running Query {:?}", sql);
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
