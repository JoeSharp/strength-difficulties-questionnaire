package uk.ratracejoe.sdq.model;

import java.util.Map;
import lombok.Builder;

@Builder
public record SdqReportingPeriod(ReportingPeriod period, Map<Assessor, SdqSubmission> sdq) {}
