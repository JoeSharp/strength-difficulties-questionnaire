use std::{collections::HashMap, io::Cursor, sync::atomic::AtomicUsize};

use calamine::{DataType, Range, Reader, Xlsx};
use sdq_model::{AceType, DisabilityType, Gender, SdqClient, SdqError};
use std::sync::atomic::Ordering;
use uuid::Uuid;

use crate::xslx_utils::{excel_cell_to_date, get_cell_number};

enum WorkbookFormat {
    Original,
    RevisedMay2026,
}

impl WorkbookFormat {
    pub fn get(demographic_sheet: &Range<DataType>) -> Result<WorkbookFormat, SdqError> {
        demographic_sheet
            .rows()
            .nth(3)
            .ok_or_else(|| WorkbookFormat::Original)
            .map(|row| {
                row.get(0)
                    .map(|cell| cell.get_string().unwrap_or_default())
                    .map(|s| s.contains("Number of sessions"))
                    .unwrap_or(false)
            })
            .map(|has_number_of_sessions| {
                if has_number_of_sessions {
                    WorkbookFormat::RevisedMay2026
                } else {
                    WorkbookFormat::Original
                }
            })
            .or_else(|_| Ok(WorkbookFormat::Original))
    }
}

const DEMOGRAPHIC_SHEET_NAME: &str = "Demographic Information";
const ROW_INTERVENTION_SESSIONS: usize = 3;
const NUMBER_INTERVENTION_TYPES: usize = 4;
const NUMBER_DISABILITY_TYPES: usize = 4;
const NUMBER_EXTENDED_ACES: usize = 9;

pub fn parse_client(
    filename: String,
    workbook: &mut Xlsx<Cursor<Vec<u8>>>,
) -> Result<SdqClient, SdqError> {
    let range = workbook
        .worksheet_range(DEMOGRAPHIC_SHEET_NAME)
        .ok_or_else(|| SdqError::Parse("Missing SDQ sheet".into()))?
        .map_err(|e| SdqError::Parse(format!("Invalid sheet: {}", e)))?;
    let format = WorkbookFormat::get(&range)?;
    let answers_row_number = match format {
        WorkbookFormat::Original => 1,
        WorkbookFormat::RevisedMay2026 => 2,
    };
    let starting_column_number = match format {
        WorkbookFormat::Original => 0,
        WorkbookFormat::RevisedMay2026 => 1,
    };

    let answers_row = range
        .rows()
        .nth(answers_row_number)
        .ok_or_else(|| SdqError::Parse(format!("Missing answers row {}", answers_row_number)))?;

    let intervention_sessions_row = range.rows().nth(ROW_INTERVENTION_SESSIONS).ok_or_else(|| {
        SdqError::Parse(format!(
            "Missing intervention sessions row {}",
            ROW_INTERVENTION_SESSIONS
        ))
    });
    let number_disability_types = match format {
        WorkbookFormat::Original => 1,
        WorkbookFormat::RevisedMay2026 => NUMBER_DISABILITY_TYPES,
    };
    let cell_num = AtomicUsize::new(starting_column_number);
    let date_of_birth = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(excel_cell_to_date)
        .flatten();
    let gender = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| s.parse::<Gender>().unwrap_or(Gender::default_value()));
    let council = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| {
            s.parse::<sdq_model::Council>()
                .unwrap_or(sdq_model::Council::default_value())
        });
    let ethnicity = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| {
            s.parse::<sdq_model::Ethnicity>()
                .unwrap_or(sdq_model::Ethnicity::default_value())
        });
    let eal = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| {
            s.parse::<sdq_model::EnglishAsAdditionalLanguage>()
                .unwrap_or(sdq_model::EnglishAsAdditionalLanguage::default_value())
        });
    let disability_status = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| {
            s.parse::<sdq_model::DisabilityStatus>()
                .unwrap_or(sdq_model::DisabilityStatus::default_value())
        });
    let mut disability_types: Vec<DisabilityType> = Vec::new();
    for _ in 0..number_disability_types {
        if let Some(cell) = answers_row.get(cell_num.fetch_add(1, Ordering::SeqCst)) {
            if let Some(s) = cell.get_string() {
                let disability_type = s
                    .parse::<DisabilityType>()
                    .unwrap_or(DisabilityType::default_value());
                if disability_type != DisabilityType::NotApplicable {
                    disability_types.push(disability_type);
                }
            }
        }
    }
    let care_experience = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| {
            s.parse::<sdq_model::CareExperience>()
                .unwrap_or(sdq_model::CareExperience::default_value())
        });

    let mut interventions: Vec<sdq_model::Intervention> = Vec::new();
    for _ in 0..NUMBER_INTERVENTION_TYPES {
        let intervention_cell_num = cell_num.fetch_add(1, Ordering::SeqCst);
        if let Some(cell) = answers_row.get(intervention_cell_num) {
            if let Some(s) = cell.get_string() {
                let intervention_type = s
                    .parse::<sdq_model::InterventionType>()
                    .unwrap_or(sdq_model::InterventionType::default_value());
                if intervention_type != sdq_model::InterventionType::Unknown {
                    let sessions = match format {
                        WorkbookFormat::Original => 0,
                        WorkbookFormat::RevisedMay2026 => intervention_sessions_row
                            .as_ref()
                            .ok()
                            .and_then(|row| row.get(intervention_cell_num))
                            .and_then(get_cell_number)
                            .unwrap_or(0),
                    };
                    interventions.push(sdq_model::Intervention {
                        r#type: intervention_type.to_string(),
                        sessions,
                    });
                }
            }
        }
    }

    let mut aces: HashMap<AceType, i32> = HashMap::new();
    let aces_generic = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .and_then(get_cell_number)
        .unwrap_or(0);
    aces.insert(AceType::Generic, aces_generic);

    if let WorkbookFormat::RevisedMay2026 = format {
        let extended_ace_headings_row =
            range.rows().nth(answers_row_number - 1).ok_or_else(|| {
                SdqError::Parse(format!(
                    "Missing extended ACE headings row {}",
                    answers_row_number - 1
                ))
            })?;
        for _ in 0..NUMBER_EXTENDED_ACES {
            let ace_cell_num = cell_num.fetch_add(1, Ordering::SeqCst);
            if let Some(column) = answers_row.get(ace_cell_num) {
                if let Some(ace_score) = get_cell_number(column) {
                    if let Some(ace_type_str) = extended_ace_headings_row.get(ace_cell_num) {
                        if let Some(ace_type_str) = ace_type_str.get_string() {
                            let ace_type = ace_type_str
                                .parse::<AceType>()
                                .unwrap_or(AceType::default_value());
                            aces.insert(ace_type, ace_score as i32);
                        }
                    }
                }
            }
        }
    }

    let funding_source = answers_row
        .get(cell_num.fetch_add(1, Ordering::SeqCst))
        .map(|cell| cell.get_string().unwrap_or_default())
        .map(|s| {
            s.parse::<sdq_model::FundingSource>()
                .unwrap_or(sdq_model::FundingSource::default_value())
        });

    Ok(SdqClient {
        client_id: Uuid::new_v4(),
        code_name: Some(filename),
        date_of_birth,
        care_experience,
        gender,
        ethnicity,
        council,
        funding_source,
        eal,
        disability_status,
        disability_types,
        interventions,
        aces,
    })
}
