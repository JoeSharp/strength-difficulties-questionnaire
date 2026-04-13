import type { Assessor } from "../types";

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
