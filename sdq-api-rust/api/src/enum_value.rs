use serde::Serialize;
use std::fmt::Display;
use strum::IntoEnumIterator;

#[derive(Debug, serde::Serialize)]
pub struct EnumValue {
    pub value: String,
    pub label: String,
}

pub fn enum_values<E>() -> Vec<EnumValue>
where
    E: IntoEnumIterator + Display + Serialize,
{
    E::iter()
        .map(|v| EnumValue {
            value: serde_json::to_string(&v)
                .unwrap()
                .trim_matches('"')
                .to_string(), // serialized form, e.g. "DAILY_GOAL"
            label: v.to_string(),
        })
        .collect()
}
