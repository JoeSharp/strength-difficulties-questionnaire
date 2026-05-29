import type { ClientFile } from "./ClientApi/clientApi";

export const ASSESSORS = ["Parent1", "Parent2", "School", "Child"] as const;
export type Assessor = (typeof ASSESSORS)[number];
export const DEFAULT_ASSESSOR: Assessor = "Parent1";

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
export type DemographicGetter = (
  client: ClientFile,
  getLabel: (field: DemographicField, value: string) => string,
) => string;
export const DEMOGRAPHIC_GETTERS: Record<DemographicField, DemographicGetter> =
  {
    Gender: (client, getLabel) => getLabel("Gender", client.gender),
    Council: (client, getLabel) => getLabel("Council", client.council),
    Ethnicity: (client, getLabel) => getLabel("Ethnicity", client.ethnicity),
    EAL: (client, getLabel) =>
      getLabel("EAL", client.englishAdditionalLanguage),
    DisabilityStatus: (client, getLabel) =>
      getLabel("DisabilityStatus", client.disabilityStatus),
    DisabilityType: (client, getLabel) =>
      client.disabilityTypes
        .map((dt) => getLabel("DisabilityType", dt))
        .join(", "),
    CareExperience: (client, getLabel) =>
      getLabel("CareExperience", client.careExperience),
    InterventionType: (client, getLabel) =>
      client.interventions
        .map((i) => `${getLabel("InterventionType", i.type)} (${i.sessions})`)
        .join(", "),
    ACES: (client, getLabel) =>
      Object.entries(client.aces)
        .map(([key, value]) => `${getLabel("ACES", key)}: ${value}`)
        .join(", "),
    FundingSource: (client, getLabel) =>
      getLabel("FundingSource", client.fundingSource),
  };

export type DemographicFilter = {
  field: DemographicField;
  values: string[];
};
