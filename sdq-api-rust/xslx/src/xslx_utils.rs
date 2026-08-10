use calamine::DataType;
use chrono::{Duration, NaiveDate};

pub fn get_cell_number(cell: &DataType) -> Option<i32> {
    match cell {
        DataType::Float(f) => Some(*f as i32),
        DataType::Int(i) => Some(*i as i32),
        DataType::String(s) => s.parse::<i32>().ok(),
        _ => None,
    }
}

pub fn excel_cell_to_date(cell: &DataType) -> Option<NaiveDate> {
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
