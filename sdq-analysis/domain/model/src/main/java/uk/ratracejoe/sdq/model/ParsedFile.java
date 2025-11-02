package uk.ratracejoe.sdq.model;

import java.util.List;
import java.util.Map;

public record ParsedFile(
    ClientFile clientFile, List<SdqPeriod> sdqPeriods, Map<Assessor, List<GboPeriod>> gboPeriods) {}
