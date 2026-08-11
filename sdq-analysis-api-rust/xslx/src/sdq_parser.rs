use chrono::NaiveDate;
use sdq_model::{SdqError, SdqReportingPeriod};
use std::io::Cursor;

use calamine::Xlsx;
pub fn parse_sdq(
    _workbook: &mut Xlsx<Cursor<Vec<u8>>>,
) -> Result<Vec<SdqReportingPeriod>, SdqError> {
    // Implement SDQ parsing logic here
    Ok(Vec::new())
}
