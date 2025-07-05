package uk.ratracejoe.sdq_analysis.database.entity;

import java.time.Instant;
import java.util.UUID;

public record ClientFileEntity(UUID uuid,
                               String filename,
                               Instant dateOfBirth,
                               String gender,
                               String council,
                               String ethnicity,
                               String englishAdditionalLanguage,
                               String disabilityStatus,
                               String disabilityType,
                               String careExperience,
                               Integer aces,
                               String fundingSource) {
}
