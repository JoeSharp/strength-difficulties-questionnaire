package uk.ratracejoe.sdq.model;

import java.util.List;
import lombok.Builder;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.sdq.SdqReportingPeriod;

@Builder
public record ParsedFile(
    SdqClient sdqClient, List<Goal> goals, List<SdqReportingPeriod> sdq, List<GboSubmission> gbo) {}
