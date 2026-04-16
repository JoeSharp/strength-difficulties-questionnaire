export type Assessor = "Parent1" | "Parent2" | "School" | "Child";

export type DemographicField =
  | "Gender"
  | "Council"
  | "Ethnicity"
  | "EAL"
  | "DisabilityStatus"
  | "DisabilityType"
  | "CareExperience"
  | "InterventionType"
  | "ACES"
  | "FundingSource";

export type DemographicFilter = {
  field: DemographicField;
  values: string[];
};
