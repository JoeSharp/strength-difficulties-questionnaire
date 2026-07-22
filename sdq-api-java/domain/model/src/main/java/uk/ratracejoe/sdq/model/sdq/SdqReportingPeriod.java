package uk.ratracejoe.sdq.model.sdq;

import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;

import java.util.Map;

@Builder
public record SdqReportingPeriod(ReportingPeriod period, Map<Assessor, SdqSubmission> sdq) {}
