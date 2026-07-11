use sdq_model::DemographicField;

pub trait ColumnName {
    fn column(&self) -> &'static str;
}

impl ColumnName for DemographicField {
    fn column(&self) -> &'static str {
        match self {
            DemographicField::Gender => "gender",
            DemographicField::Council => "council",
            DemographicField::Ethnicity => "ethnicity",
            DemographicField::EAL => "eal",
            DemographicField::DisabilityStatus => "disability_status",
            DemographicField::DisabilityType => "disability_type",
            DemographicField::CareExperience => "care_experience",
            DemographicField::ACES => "aces",
            DemographicField::FundingSource => "funding_source",
            _ => "foo", // sort this out!
        }
    }
}
