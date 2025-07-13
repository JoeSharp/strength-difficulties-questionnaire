export type DemographicField =
  | "Gender"
  | "Council"
  | "Ethnicity"
  | "English as an Additional Language"
  | "Disability Status"
  | "Disability Type"
  | "Care Experience"
  | "Intervention Type"
  | "ACES"
  | "Funding Source"
  | "UNKNOWN";

export type DemographicReference = {
  [key in keyof DemographicField]?: string[];
};

const EMPTY_DEMOGRAPHIC_REFERENCE: DemographicReference = {};

export interface ClientFile {
  uuid: string;
  filename: string;
  dateOfBirth: Date;
  gender: string;
  council: string;
  ethnicity: string;
  englishAdditionalLanguage: string;
  disabilityStatus: string;
  disabilityType: string;
  careExperience: string;
  interventionTypes: string[];
  aces: number;
  fundingSource: string;
}

export const EMPTY_CLIENT_FILE: ClientFile = {
  uuid: "",
  filename: "",
  dateOfBirth: new Date(),
  gender: "",
  council: "",
  ethnicity: "",
  englishAdditionalLanguage: "",
  disabilityStatus: "",
  disabilityType: "",
  careExperience: "",
  interventionTypes: [],
  aces: 0,
  fundingSource: "",
};

export interface SdqSummary {
  uuid: string;
  period: number;
  categoryScores: Record<string, number>;
  postureScores: Record<string, number>;
  total: number;
}

export interface GboSummary {
  uuid: string;
  periodIndex: number;
  periodDate: Date;
  scores: Record<number, number>;
}

export type Assessor = "Parent1" | "Parent2" | "School" | "Child";

export type SdqSummaryByAssessor = Record<Assessor, SdqSummary[]>;
export type GboSummaryByAssessor = Record<Assessor, GboSummary[]>;

export const EMPTY_SDQ: SdqSummaryByAssessor = {
  Parent1: [],
  Parent2: [],
  Child: [],
  School: [],
};
export const EMPTY_GBQ: GboSummaryByAssessor = {
  Parent1: [],
  Parent2: [],
  Child: [],
  School: [],
};

export type Statement =
  | "CONSIDERATE"
  | "RESTLESS"
  | "COMPLAINS_ACHES"
  | "SHARES_READILY"
  | "TEMPER"
  | "SOLITARY"
  | "OBEDIENT"
  | "WORRIES"
  | "HELPFUL"
  | "FIDGETING"
  | "ONE_GOOD_FRIEND"
  | "FIGHTS"
  | "UNHAPPY"
  | "LIKED_BY_OTHERS"
  | "DISTRACTED"
  | "NERVOUS"
  | "KIND_TO_YOUNGER"
  | "LIES"
  | "PICKED_ON"
  | "VOLUNTEERS"
  | "THINKS_THROUGH"
  | "STEALS"
  | "GETS_ON_ADULTS_BETTER"
  | "FEARS"
  | "ATTENTION";

export const CATEGORIES: string[] = [
  "Conduct",
  "Peer",
  "HyperActivity",
  "ProSocial",
  "Emotional",
];

export const POSTURES: string[] = [
  "Internalising",
  "Externalising",
  "ProSocial",
];

export interface StatementResponse {
  statement: Statement;
  score: number;
}

export interface SdqPeriod {
  periodIndex: number;
  responses: Record<Assessor, StatementResponse[]>;
}

export interface ParsedFile {
  clientFile: ClientFile;
  sdqPeriods: SdqPeriod[];
}

export const EMPTY_PARSED_FILE: ParsedFile = {
  clientFile: EMPTY_CLIENT_FILE,
  sdqPeriods: [],
};

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
