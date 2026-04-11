package uk.ratracejoe.sdq.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import uk.ratracejoe.sdq.model.demographics.*;

@Builder
public record SdqClient(
    UUID clientId,
    String codeName,
    LocalDate dateOfBirth,
    Gender gender,
    Council council,
    Ethnicity ethnicity,
    EnglishAsAdditionalLanguage englishAdditionalLanguage,
    DisabilityStatus disabilityStatus,
    DisabilityType disabilityType,
    CareExperience careExperience,
    @With List<InterventionType> interventionTypes,
    Integer aces,
    FundingSource fundingSource) {}
