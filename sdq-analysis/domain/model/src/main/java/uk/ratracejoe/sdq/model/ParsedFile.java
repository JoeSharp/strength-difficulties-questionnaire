package uk.ratracejoe.sdq.model;

import java.util.List;

public record ParsedFile(SdqClient sdqClient, List<SdqSubmission> sdq, List<GboSubmission> gbo) {}
