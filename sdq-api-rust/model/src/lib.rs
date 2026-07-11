use std::collections::HashMap;

use serde::{Deserialize, Serialize};
use strum::{Display, EnumIter, EnumString};

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
