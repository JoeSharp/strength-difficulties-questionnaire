use sdq_model::{ParsedFile, SdqError};

pub trait ParserService {
    fn parse_file(&self, filename: String, data: Vec<u8>) -> Result<ParsedFile, SdqError>;
}
