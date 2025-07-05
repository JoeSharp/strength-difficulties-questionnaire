package uk.ratracejoe.sdq_analysis.dto;

import java.util.List;

public record ParsedFile(ClientFile clientFile, List<SdqPeriod> sdqPeriods) {
}
