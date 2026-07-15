package uk.ratracejoe.sdq.model.sdq;

import java.util.Map;
import lombok.Builder;
import uk.ratracejoe.sdq.model.Assessor;
import uk.ratracejoe.sdq.model.ReportingPeriod;

@Builder
public record SdqReportingPeriod(ReportingPeriod period, Map<Assessor, SdqSubmission> sdq) {}
