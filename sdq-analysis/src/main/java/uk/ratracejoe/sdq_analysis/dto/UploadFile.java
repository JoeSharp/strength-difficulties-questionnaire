package uk.ratracejoe.sdq_analysis.dto;

import java.util.UUID;

public record UploadFile(UUID uuid,
                         String filename,
                         String dateOfBirth,
                         Gender gender,
                         Ethnicity ethnicity,
                         YesNoAbstain englishAdditionalLanguage,
                         YesNoAbstain disabilityStatus,
                         DisabilityType disabilityType,
                         CareExperience careExperience,
                         InterventionType interventionType,
                         Aces aces,
                         FundingSource fundingSource) {
}
