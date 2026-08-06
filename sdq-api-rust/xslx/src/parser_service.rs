use std::{collections::HashMap, io::Cursor, sync::atomic::AtomicUsize};

use calamine::{DataType, Range, Reader, Xlsx};
use chrono::{Duration, NaiveDate};
use sdq_model::{
    AceType, Assessor, DisabilityType, Gender, GoalType, ParsedFile, SdqClient, SdqError,
    SdqReportingPeriod,
};
use sdq_service::parser::ParserService;
use std::sync::atomic::Ordering;
use uuid::Uuid;

pub struct ParserServiceXslxImpl {}

impl ParserServiceXslxImpl {
    pub fn new() -> ParserServiceXslxImpl {
        ParserServiceXslxImpl {}
    }
}

struct GboParsedScore {
    goal_type: GoalType,
    index: usize,
    score: i32,
}

struct GboParsedPeriod {
    assessor: Assessor,
    period: NaiveDate,
    scores: Vec<GboParsedScore>,
}

impl ParserService for ParserServiceXslxImpl {
    fn parse_file(&self, filename: String, data: Vec<u8>) -> Result<ParsedFile, SdqError> {
        let reader = std::io::Cursor::new(data);

        let mut workbook = Xlsx::new(reader)
            .map_err(|e| SdqError::Parse(format!("Failed to open workbook: {}", e)))?;

        let client = self.parse_client(filename, &mut workbook)?;
        let gbo = self.parse_gbo(&mut workbook)?;
        let sdq = self.parse_sdq(&mut workbook)?;

        let mut parsed = ParsedFile {
            client,
            gbo: Vec::new(),
            sdq,
            goals: Vec::new(),
        };

        Ok(parsed)
    }
}

impl ParserServiceXslxImpl {
    fn parse_gbo(
        &self,
        workbook: &mut Xlsx<Cursor<Vec<u8>>>,
    ) -> Result<Vec<GboParsedPeriod>, SdqError> {
        // Implement GBO parsing logic here
        Ok(Vec::new())
    }
}

impl ParserServiceXslxImpl {
    fn parse_sdq(
        &self,
        workbook: &mut Xlsx<Cursor<Vec<u8>>>,
    ) -> Result<Vec<SdqReportingPeriod>, SdqError> {
        // Implement SDQ parsing logic here
        Ok(Vec::new())
    }
}

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

impl ParserServiceXslxImpl {
    const DEMOGRAPHIC_SHEET_NAME: &str = "Demographic Information";
    const ROW_INTERVENTION_SESSIONS: usize = 3;
    const NUMBER_INTERVENTION_TYPES: usize = 4;
    const NUMBER_DISABILITY_TYPES: usize = 4;
    const NUMBER_EXTENDED_ACES: usize = 9;

    fn parse_client(
        &self,
        filename: String,
        workbook: &mut Xlsx<Cursor<Vec<u8>>>,
    ) -> Result<SdqClient, SdqError> {
        let range = workbook
            .worksheet_range(Self::DEMOGRAPHIC_SHEET_NAME)
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

        let answers_row = range.rows().nth(answers_row_number).ok_or_else(|| {
            SdqError::Parse(format!("Missing answers row {}", answers_row_number))
        })?;

        let intervention_sessions_row = range
            .rows()
            .nth(Self::ROW_INTERVENTION_SESSIONS)
            .ok_or_else(|| {
                SdqError::Parse(format!(
                    "Missing intervention sessions row {}",
                    Self::ROW_INTERVENTION_SESSIONS
                ))
            });
        let number_disability_types = match format {
            WorkbookFormat::Original => 1,
            WorkbookFormat::RevisedMay2026 => Self::NUMBER_DISABILITY_TYPES,
        };
        let cell_num = AtomicUsize::new(starting_column_number);
        let date_of_birth = answers_row
            .get(cell_num.fetch_add(1, Ordering::SeqCst))
            .map(Self::excel_cell_to_date)
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
        for _ in 0..Self::NUMBER_INTERVENTION_TYPES {
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
                                .and_then(|cell| cell.get_int())
                                .map(|f| f as i32)
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
            .and_then(|cell| match cell {
                DataType::Float(f) => Some(*f as i32),
                DataType::Int(i) => Some(*i as i32),
                DataType::String(s) => s.parse::<i32>().ok(),
                _ => None,
            })
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
            for _ in 0..Self::NUMBER_EXTENDED_ACES {
                if let Some(column) = answers_row.get(cell_num.fetch_add(1, Ordering::SeqCst)) {
                    if let Some(ace_score) = column.get_int() {
                        if let Some(ace_type_str) =
                            extended_ace_headings_row.get(cell_num.load(Ordering::SeqCst))
                        {
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

    fn excel_cell_to_date(cell: &DataType) -> Option<NaiveDate> {
        match cell {
            // Excel stores dates as floating-point days since 1899-12-30
            DataType::Float(days) => {
                let excel_epoch = NaiveDate::from_ymd_opt(1899, 12, 30)?;
                Some(excel_epoch + Duration::days(*days as i64))
            }

            // If the sheet contains ISO strings
            DataType::String(s) => NaiveDate::parse_from_str(s, "%Y-%m-%d").ok(),

            // Calamine sometimes gives DateTime directly
            DataType::DateTime(dt) => {
                let excel_epoch = NaiveDate::from_ymd_opt(1899, 12, 30)?;
                Some(excel_epoch + Duration::days(*dt as i64))
            }

            _ => None,
        }
    }
}
