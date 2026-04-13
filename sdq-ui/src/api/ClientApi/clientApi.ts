export const BASE_CLIENT_URL = "/api/client";

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

export function parseFile(file: ClientFile): ClientFile {
  return {
    ...file,
    dateOfBirth: new Date(file.dateOfBirth),
  };
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
