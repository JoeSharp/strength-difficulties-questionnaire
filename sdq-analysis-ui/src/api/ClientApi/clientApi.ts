import type { DemographicFilter } from "../types";

export const BASE_CLIENT_URL = "/api/client";

export type Intervention = {
  type: string;
  sessions: number;
};

export type ClientFile = {
  clientId: string;
  codeName: string;
  dateOfBirth: Date;
  gender: string;
  council: string;
  ethnicity: string;
  englishAdditionalLanguage: string;
  disabilityStatus: string;
  disabilityTypes: string[];
  careExperience: string;
  interventions: Intervention[];
  aces: Record<string, number>;
  fundingSource: string;
};

export const EMPTY_CLIENT_FILE: ClientFile = {
  clientId: "",
  codeName: "",
  dateOfBirth: new Date(),
  gender: "",
  council: "",
  ethnicity: "",
  englishAdditionalLanguage: "",
  disabilityStatus: "",
  disabilityTypes: [],
  careExperience: "",
  interventions: [],
  aces: {},
  fundingSource: "",
};

export function parseFile(file: ClientFile): ClientFile {
  return {
    ...file,
    dateOfBirth: new Date(file.dateOfBirth),
  };
}

export type DemographicCount = {
  option: string;
  count: number;
  percentage: number;
};

export type DemographicReport = {
  counts: DemographicCount[];
};

export const EMPTY_DEMOGRAPHIC_REPORT: DemographicReport = {
  counts: [],
};

export type ClientQueryDTO = {
  partialName: string;
  filters: DemographicFilter[];
};
export const DEFAULT_CLIENT_QUERY: ClientQueryDTO = {
  partialName: "",
  filters: [],
};
