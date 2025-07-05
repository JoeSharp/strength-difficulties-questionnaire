package uk.ratracejoe.sdq_analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.dto.ClientFile;
import uk.ratracejoe.sdq_analysis.dto.SdqPeriod;
import uk.ratracejoe.sdq_analysis.dto.SdqScoresSummary;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SdqResponseService {
    private final SdqResponseRepository sdqResponseRepository;

    public List<SdqScoresSummary> getScores() {

        return Collections.emptyList();
    }

    public void recordResponse(ClientFile file,
                               List<SdqPeriod> periods) throws SdqException {
        periods.forEach(period ->
                period.responses().forEach((key, value) -> value.forEach(response -> {

                })));
    }
}
