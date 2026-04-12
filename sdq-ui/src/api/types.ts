export type Assessor = "Parent1" | "Parent2" | "School" | "Child";

export interface ClientFile {
  clientId: string;
  codeName: string;
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
  clientId: "",
  codeName: "",
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

export interface SdqScore {
  statement: string;
  score: number;
}

export interface SdqSubmission {
  clientId: string;
  period: number;
  assessor: Assessor;
  scores: SdqScore[];
}

export interface DemographicCount {
  option: string;
  count: number;
  percentage: number;
}

export interface DemographicReport {
  counts: DemographicCount[];
}

export const EMPTY_DEMOGRAPHIC_REPORT: DemographicReport = {
  counts: [],
};

export interface GboScore {
  scoreIndex: number;
  score: number;
}

export interface GboSubmission {
  clientId: string;
  assessor: Assessor;
  periodDate: Date;
  scores: GboScore[];
}

export const STATEMENT = [
  "CONSIDERATE",
  "RESTLESS",
  "COMPLAINS_ACHES",
  "SHARES_READILY",
  "TEMPER",
  "SOLITARY",
  "OBEDIENT",
  "WORRIES",
  "HELPFUL",
  "FIDGETING",
  "ONE_GOOD_FRIEND",
  "FIGHTS",
  "UNHAPPY",
  "LIKED_BY_OTHERS",
  "DISTRACTED",
  "NERVOUS",
  "KIND_TO_YOUNGER",
  "LIES",
  "PICKED_ON",
  "VOLUNTEERS",
  "THINKS_THROUGH",
  "STEALS",
  "GETS_ON_ADULTS_BETTER",
  "FEARS",
  "ATTENTION",
] as const;
export type Statement = (typeof STATEMENT)[number];

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
  sdq: SdqScore[];
  gbo: GboScore[];
}

export const EMPTY_PARSED_FILE: ParsedFile = {
  clientFile: EMPTY_CLIENT_FILE,
  sdq: [],
  gbo: [],
};
