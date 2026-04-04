package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SdqClient(
    UUID clientId,
    String codeName,
    Instant dateOfBirth,
    Gender gender,
    Council council,
    Ethnicity ethnicity,
    EnglishAsAdditionalLanguage englishAdditionalLanguage,
    DisabilityStatus disabilityStatus,
    DisabilityType disabilityType,
    CareExperience careExperience,
    List<InterventionType> interventionTypes,
    Integer aces,
    FundingSource fundingSource) {}
