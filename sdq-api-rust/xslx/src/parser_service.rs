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
        Err(SdqError::NotImplemented)
    }
}
