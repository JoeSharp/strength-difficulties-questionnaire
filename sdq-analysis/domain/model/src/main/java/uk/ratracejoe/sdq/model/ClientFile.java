package uk.ratracejoe.sdq.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ClientFile(
    UUID uuid,
    String filename,
    Instant dateOfBirth,
    String gender,
    String council,
    String ethnicity,
    String englishAdditionalLanguage,
    String disabilityStatus,
    String disabilityType,
    String careExperience,
    List<String> interventionTypes,
    Integer aces,
    String fundingSource) {}
