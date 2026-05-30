import type { Assessor, DemographicFilter } from "../types";

export const BASE_SDQ_URL = "/api/sdq";

export type SdqScore = {
  statement: string;
  score: number;
};

export type SdqSubmission = {
  clientId: string;
  period: number;
  assessor: Assessor;
  scores: SdqScore[];
};

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

export type StatementResponse = {
  statement: Statement;
  score: number;
};

export type ReportingPeriod = {
  clientId: string;
  periodId: string;
  period: string;
};

export type SdqReportingPeriod = {
  period: ReportingPeriod;
  responses: Record<Assessor, SdqSubmission[]>;
};

export type SdqSubmissionSummary = {
  clientId: string;
  assessor: Assessor;
  period: string;
  categorySubTotals: Record<string, number>;
  postureSubTotals: Record<string, number>;
  totalDifficulties: number;
};

export type SdqSubmissionSummaryByPeriod = Record<
  string,
  SdqSubmissionSummary[]
>;

export type SdqSubmissionSummaryByClient = Record<
  string,
  SdqSubmissionSummaryByPeriod
>;

export type Progress = {
  last: number;
  first: number;
  delta: number;
};

export type SdqProgressSummary = {
  clientId: string;
  assessor: Assessor;
  categoryProgress: Record<string, Progress>;
  postureProgress: Record<string, Progress>;
  totalDifficulties: Progress;
};

export type SdqProgressSummaryByClient = Record<string, SdqProgressSummary[]>;

export type SdqQueryDTO = {
  assessors: Assessor[];
  filters: DemographicFilter[];
  from: string;
  to: string;
};
