package uk.ratracejoe.sdq.model;

import java.util.List;

public record ParsedFile(
    SdqClient sdqClient, List<SdqScore> sdq, List<GboScore> gbo, SdqEnumerations structure) {}
