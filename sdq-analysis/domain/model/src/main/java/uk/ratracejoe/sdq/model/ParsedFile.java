package uk.ratracejoe.sdq.model;

import java.util.List;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.sdq.SdqReportingPeriod;

public record ParsedFile(
    SdqClient sdqClient, List<SdqReportingPeriod> sdq, List<GboSubmission> gbo) {}
