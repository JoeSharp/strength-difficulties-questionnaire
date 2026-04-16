export const DEMOGRAPHIC_FIELDS = [
  "Gender",
  "Council",
  "Ethnicity",
  "EAL",
  "DisabilityStatus",
  "DisabilityType",
  "CareExperience",
  "InterventionType",
  "ACES",
  "FundingSource",
] as const;
export type DemographicField = (typeof DEMOGRAPHIC_FIELDS)[number];
export type DemographicReference = {
  [key in DemographicField]?: string[];
};

const EMPTY_DEMOGRAPHIC_REFERENCE: DemographicReference = {};

export type RefInfoDescription = Record<string, Record<string, object>>;
const EMPTY_REF_INFO_DESCRIPTION: RefInfoDescription = {};

export interface ReferenceInfo {
  categories: RefInfoDescription;
  statements: RefInfoDescription;
  postures: RefInfoDescription;
  demographicFields: DemographicReference;
}

export const EMPTY_REFERENCE_INFO: ReferenceInfo = {
  categories: EMPTY_REF_INFO_DESCRIPTION,
  statements: EMPTY_REF_INFO_DESCRIPTION,
  postures: EMPTY_REF_INFO_DESCRIPTION,
  demographicFields: EMPTY_DEMOGRAPHIC_REFERENCE,
};
