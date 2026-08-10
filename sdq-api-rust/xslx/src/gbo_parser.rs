use chrono::NaiveDate;
use sdq_model::{Assessor, GboSubmission, Goal, GoalType, SdqError};
use std::io::Cursor;

use calamine::Xlsx;
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

pub fn parse_gbo(
    _workbook: &mut Xlsx<Cursor<Vec<u8>>>,
) -> Result<(Vec<GboSubmission>, Vec<Goal>), SdqError> {
    // Implement GBO parsing logic here
    Ok((Vec::new(), Vec::new()))
}
