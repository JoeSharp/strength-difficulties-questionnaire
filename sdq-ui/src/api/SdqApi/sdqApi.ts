import type { Assessor, DemographicFilter } from "../types";

export const BASE_SDQ_URL = "/api/sdq";

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

export interface ReportingPeriod {
  clientId: string;
  periodId: string;
  period: string;
}

export interface SdqReportingPeriod {
  period: ReportingPeriod;
  responses: Record<Assessor, SdqSubmission[]>;
}

export interface SdqSubmissionSummary {
  clientId: string;
  period: string;
  categorySubTotals: Record<string, number>;
  postureSubTotals: Record<string, number>;
  totalDifficulties: number;
}

export type Progress = {
  last: number;
  first: number;
  delta: number;
};

export interface SdqProgressSummary {
  clientId: string;
  assessor: Assessor;
  categoryProgress: Record<string, Progress>;
  postureProgress: Record<string, Progress>;
  totalDifficulties: Progress;
}

export interface SdqFilterDTO {
  assessor: Assessor;
  filters: DemographicFilter[];
  from: string;
  to: string;
}
