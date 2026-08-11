package uk.ratracejoe.sdq.model.gbo;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;

@Builder
public record GboSubmission(UUID goalId, LocalDate period, Assessor assessor, Integer score) {}
