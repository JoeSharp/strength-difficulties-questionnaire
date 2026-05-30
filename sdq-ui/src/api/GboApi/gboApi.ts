import type { Assessor, DemographicFilter } from "../types";

export const BASE_GOAL_URL = "/api/goal";

export interface Goal {
  clientId: string;
  type: string;
  goalId: string;
  description: string;
}

export interface GoalProgress {
  goal: Goal;
  assessor: Assessor;
  firstScore: number;
  lastScore: number;
}

export interface GboSubmission {
  goalId: string;
  period: Date;
  assessor: Assessor;
  score: number;
}

export interface GoalQueryDTO {
  goalTypes: string[];
  assessors: Assessor[];
  filters: DemographicFilter[];
  minProgress: number;
  from: string;
  to: string;
}
