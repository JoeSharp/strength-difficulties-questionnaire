import type { ClientFile } from "./ClientApi/clientApi";

export type Assessor = "Parent1" | "Parent2" | "School" | "Child";

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
export type DemographicGetter = (client: ClientFile) => string;
export const DEMOGRAPHIC_GETTERS: Record<DemographicField, DemographicGetter> =
  {
    Gender: (client) => client.gender,
    Council: (client) => client.council,
    Ethnicity: (client) => client.ethnicity,
    EAL: (client) => client.englishAdditionalLanguage,
    DisabilityStatus: (client) => client.disabilityStatus,
    DisabilityType: (client) => client.disabilityTypes.join(", "),
    CareExperience: (client) => client.careExperience,
    InterventionType: (client) =>
      client.interventions.map((i) => `${i.type} (${i.sessions})`).join(", "),
    ACES: (client) => client.aces.toString(),
    FundingSource: (client) => client.fundingSource,
  };

export type DemographicFilter = {
  field: DemographicField;
  values: string[];
};
