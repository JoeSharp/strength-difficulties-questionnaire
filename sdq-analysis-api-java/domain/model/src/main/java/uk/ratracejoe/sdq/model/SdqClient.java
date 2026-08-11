package uk.ratracejoe.sdq.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
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
    List<DisabilityType> disabilityTypes,
    CareExperience careExperience,
    List<Intervention> interventions,
    Map<AceType, Integer> aces,
    FundingSource fundingSource) {}
