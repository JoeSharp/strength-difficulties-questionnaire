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
export type Option<T = string> = {
  value: T;
  label: string;
};

export type DemographicReference = {
  [key in DemographicField]?: Option[];
};

export type Category = {
  category: string;
  posture: string;
};

export type Statement = {
  order: number;
  key: string;
  category: Category;
  isTruePositive: boolean;
  description: string;
};

export interface ReferenceInfo {
  goalTypes: Option[];
  categories: Category[];
  statements: Statement[];
  postures: string[];
  demographicFields: DemographicReference;
}

export const EMPTY_REFERENCE_INFO: ReferenceInfo = {
  goalTypes: [],
  categories: [],
  statements: [],
  postures: [],
  demographicFields: {},
};
