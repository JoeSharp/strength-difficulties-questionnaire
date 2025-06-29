package uk.ratracejoe.sdq_analysis.dto;

import java.util.List;

public record ParsedFile(UploadFile uploadFile, List<SdqPeriod> sdqPeriods) {
}
