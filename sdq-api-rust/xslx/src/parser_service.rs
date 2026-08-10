use crate::demographic_parser::parse_client;
use crate::gbo_parser::parse_gbo;
use crate::sdq_parser::parse_sdq;
use calamine::{Reader, Xlsx};
use sdq_model::{ParsedFile, SdqError};
use sdq_service::parser::ParserService;

pub struct ParserServiceXslxImpl {}

impl ParserServiceXslxImpl {
    pub fn new() -> ParserServiceXslxImpl {
        ParserServiceXslxImpl {}
    }
}

impl ParserService for ParserServiceXslxImpl {
    fn parse_file(&self, filename: String, data: Vec<u8>) -> Result<ParsedFile, SdqError> {
        let reader = std::io::Cursor::new(data);

        let mut workbook = Xlsx::new(reader)
            .map_err(|e| SdqError::Parse(format!("Failed to open workbook: {}", e)))?;

        let client = parse_client(filename, &mut workbook)?;
        let (gbo, goals) = parse_gbo(&mut workbook)?;
        let sdq = parse_sdq(&mut workbook)?;

        Ok(ParsedFile {
            client,
            gbo,
            goals,
            sdq,
        })
    }
}
