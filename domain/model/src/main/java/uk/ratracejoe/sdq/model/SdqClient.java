package uk.ratracejoe.sdq.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    @With List<DisabilityType> disabilityTypes,
    CareExperience careExperience,
    @With List<Intervention> interventions,
    @With Map<AceType, Integer> aces,
    FundingSource fundingSource) {}
