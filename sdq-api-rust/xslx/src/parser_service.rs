use std::{collections::HashMap, io::Cursor};

use calamine::{Reader, Xlsx};
use chrono::NaiveDate;
use sdq_model::{Assessor, GoalType, ParsedFile, SdqClient, SdqError, SdqReportingPeriod};
use sdq_service::parser::ParserService;

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

    fn parse_sdq(
        &self,
        workbook: &mut Xlsx<Cursor<Vec<u8>>>,
    ) -> Result<Vec<SdqReportingPeriod>, SdqError> {
        // Implement SDQ parsing logic here
        Ok(Vec::new())
    }

    fn parse_client(
        &self,
        filename: String,
        workbook: &mut Xlsx<Cursor<Vec<u8>>>,
    ) -> Result<SdqClient, SdqError> {
        let sheet_name = "Demographic Information"; // whatever your sheet is called
        let range = workbook
            .worksheet_range(sheet_name)
            .ok_or_else(|| SdqError::Parse("Missing SDQ sheet".into()))?
            .map_err(|e| SdqError::Parse(format!("Invalid sheet: {}", e)))?;
        for row in range.rows() {
            // Example: read columns
            let statement_key = row[0].to_string();
            let score_value = row[1].to_string().parse::<i32>().unwrap_or(0);

            //parsed.add_score(statement_key, score_value);
        }
        Ok(SdqClient {
            code_name: Some(filename),
            date_of_birth: None,
            care_experience: None,
            client_id: None,
            gender: None,
            ethnicity: None,
            council: None,
            funding_source: None,
            eal: None,
            disability_status: None,
            disability_types: Vec::new(),
            interventions: Vec::new(),
            aces: HashMap::new(),
        })
        /*
        Err(SdqError::Parse(
            "parse_client not implemented for XLSX".into(),
        ))
        */
    }
}
