use axum::Json;
use axum::{Router, routing::get};
use sdq_model::{
    AceType, CareExperience, Council, DemographicField, DisabilityStatus, DisabilityType,
    EnglishAsAdditionalLanguage, Ethnicity, FundingSource, Gender, GoalType, InterventionType,
    Posture,
};
use std::collections::HashMap;

use crate::build_api::AppState;
use crate::enum_value::{EnumValue, enum_values};

#[derive(Debug, serde::Serialize)]
#[serde(rename_all = "camelCase")]
struct ReferenceInfoDTO {
    pub goal_types: Vec<EnumValue>,
    pub postures: Vec<EnumValue>,
    pub demographic_fields: HashMap<DemographicField, Vec<EnumValue>>,
}

async fn reference_info_handler() -> Json<ReferenceInfoDTO> {
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

pub fn build_reference_api() -> Router<AppState> {
    Router::new().route("/", get(reference_info_handler))
}
