package uk.ratracejoe.sdq.model;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReportingPeriod(UUID clientId, UUID periodId, LocalDate period) {}
