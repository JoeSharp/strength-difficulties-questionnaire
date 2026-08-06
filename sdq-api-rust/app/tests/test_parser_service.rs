use std::collections::HashMap;

use chrono::NaiveDate;
use sdq_model::{
    AceType, CareExperience, Council, DisabilityStatus, DisabilityType,
    EnglishAsAdditionalLanguage, Ethnicity, FundingSource, Gender, Intervention, InterventionType,
};
use sdq_service::parser::ParserService;
use sdq_xslx::parser_service::ParserServiceXslxImpl;

#[tokio::test]
async fn test_xslx_parser_original() {
    let service = ParserServiceXslxImpl::new();
    let bytes = std::fs::read("tests/data/Test File 1.xlsx").expect("failed to load test file");
    let parsed = service
        .parse_file("test_file".to_string(), bytes)
        .expect("Failed to parse test file");
    assert_eq!(parsed.client.code_name, Some("test_file".to_string()));
    assert_eq!(
        parsed.client.date_of_birth,
        Some(NaiveDate::from_ymd_opt(1983, 8, 1).unwrap())
    );
    assert_eq!(parsed.client.gender, Some(Gender::Male));
    assert_eq!(parsed.client.council, Some(Council::GloucesterCity));
    assert_eq!(parsed.client.ethnicity, Some(Ethnicity::WhiteBritish));
    assert_eq!(parsed.client.eal, Some(EnglishAsAdditionalLanguage::No));
    assert_eq!(
        parsed.client.disability_status,
        Some(DisabilityStatus::Disability)
    );
    assert_eq!(
        parsed.client.disability_types,
        vec![DisabilityType::Sensory]
    );
    assert_eq!(
        parsed.client.care_experience,
        Some(CareExperience::YesAdopted)
    );
    assert_eq!(
        parsed.client.interventions,
        vec![
            Intervention {
                r#type: InterventionType::CPRT.to_string(),
                sessions: 0,
            },
            Intervention {
                r#type: InterventionType::PTP.to_string(),
                sessions: 0,
            },
            Intervention {
                r#type: InterventionType::IA.to_string(),
                sessions: 0,
            },
            Intervention {
                r#type: InterventionType::CPRT.to_string(),
                sessions: 0,
            },
        ]
    );
    let expected_aces: HashMap<AceType, i32> = [(AceType::Generic, 3)].into_iter().collect();
    assert_eq!(parsed.client.aces, expected_aces);
    assert_eq!(parsed.client.funding_source, Some(FundingSource::ASGSF));
}

#[tokio::test]
async fn test_xslx_parser_revised() {
    let service = ParserServiceXslxImpl::new();
    let bytes = std::fs::read("tests/data/Master Data Record for 09.05.26 Revised.xlsx")
        .expect("failed to load test file");
    let parsed = service
        .parse_file("test_file".to_string(), bytes)
        .expect("Failed to parse test file");
    assert_eq!(parsed.client.code_name, Some("test_file".to_string()));
    assert_eq!(
        parsed.client.date_of_birth,
        Some(NaiveDate::from_ymd_opt(1983, 8, 16).unwrap())
    );
    assert_eq!(parsed.client.gender, Some(Gender::NonBinary));
    assert_eq!(parsed.client.council, Some(Council::ForestOfDean));
    assert_eq!(parsed.client.ethnicity, Some(Ethnicity::Asian));
    assert_eq!(parsed.client.eal, Some(EnglishAsAdditionalLanguage::No));
    assert_eq!(
        parsed.client.disability_status,
        Some(DisabilityStatus::Disability)
    );
    assert_eq!(
        parsed.client.disability_types,
        vec![
            DisabilityType::Learning,
            DisabilityType::CognitiveOrMemory,
            DisabilityType::MentalHealthCondition
        ]
    );
    assert_eq!(
        parsed.client.care_experience,
        Some(CareExperience::YesAdopted)
    );
    assert_eq!(
        parsed.client.interventions,
        vec![
            Intervention {
                r#type: InterventionType::CCPT.to_string(),
                sessions: 3,
            },
            Intervention {
                r#type: InterventionType::PTP.to_string(),
                sessions: 2,
            },
            Intervention {
                r#type: InterventionType::CPRT.to_string(),
                sessions: 8,
            },
            Intervention {
                r#type: InterventionType::IA.to_string(),
                sessions: 4,
            },
        ]
    );
    let expected_aces: HashMap<AceType, i32> = [
        (AceType::Generic, 6),
        (AceType::Community, 4),
        (AceType::SocioEconomic, 2),
        (AceType::Health, 8),
        (AceType::Bereavement, 4),
        (AceType::ChildWelfare, 10),
    ]
    .into_iter()
    .collect();
    assert_eq!(parsed.client.aces, expected_aces);
}
