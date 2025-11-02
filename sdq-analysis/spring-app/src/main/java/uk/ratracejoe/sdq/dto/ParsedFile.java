package uk.ratracejoe.sdq.dto;

import java.util.List;
import java.util.Map;

public record ParsedFile(
    ClientFile clientFile, List<SdqPeriod> sdqPeriods, Map<Assessor, List<GboPeriod>> gboPeriods) {}
