package uk.ratracejoe.sdq.model;

import java.util.List;

public record ParsedFile(
    ClientFile clientFile, List<SdqScore> sdq, List<GboScore> gbo, SdqEnumerations structure) {}
