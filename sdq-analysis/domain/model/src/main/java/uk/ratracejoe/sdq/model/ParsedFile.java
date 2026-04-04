package uk.ratracejoe.sdq.model;

import java.util.List;

public record ParsedFile(
    SdqClient sdqClient, List<SdqReportingPeriod> sdq, List<GboSubmission> gbo) {}
