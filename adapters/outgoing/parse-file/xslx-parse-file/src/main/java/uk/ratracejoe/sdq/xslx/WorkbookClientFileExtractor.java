package uk.ratracejoe.sdq.xslx;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.GboParsedPeriod;
import uk.ratracejoe.sdq.model.GboParsedScore;
import uk.ratracejoe.sdq.model.ParsedFile;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.gbo.GboSubmission;
import uk.ratracejoe.sdq.model.gbo.Goal;
import uk.ratracejoe.sdq.model.sdq.SdqReportingPeriod;

@RequiredArgsConstructor
public class WorkbookClientFileExtractor {
  private final WorkbookSdqExtractor xslSdqExtractor;
  private final WorkbookGboExtractor workbookGboExtractor;
  private final WorkbookDemographicExtractor xslDemographicExtractor;

  public ParsedFile extract(String name, Workbook workbook) throws SdqException {
    SdqClient sdqClient = xslDemographicExtractor.parse(workbook, name);
    List<SdqReportingPeriod> sdq = xslSdqExtractor.parse(sdqClient.clientId(), workbook);
    List<GboParsedPeriod> parsedGbo = workbookGboExtractor.parse(workbook);

    List<Integer> goalIndices =
        parsedGbo.stream()
            .flatMap(p -> p.scores().stream())
            .filter(Objects::nonNull)
            .map(GboParsedScore::index)
            .distinct()
            .sorted()
            .toList();

    Map<Integer, Goal> goalsByIndex =
        goalIndices.stream()
            .collect(
                Collectors.toMap(
                    i -> i,
                    i ->
                        Goal.builder()
                            .goalId(UUID.randomUUID())
                            .clientId(sdqClient.clientId())
                            .description(String.format("Goal %d for %s", i, sdqClient.codeName()))
                            .build()));

    List<Goal> goals = goalIndices.stream().map(goalsByIndex::get).toList();
    List<GboSubmission> gbo =
        parsedGbo.stream()
            .flatMap(
                p ->
                    p.scores().stream()
                        .map(
                            s ->
                                GboSubmission.builder()
                                    .period(p.period())
                                    .assessor(p.assessor())
                                    .goalId(goalsByIndex.get(s.index()).goalId())
                                    .score(s.score())
                                    .build()))
            .toList();

    return ParsedFile.builder().goals(goals).gbo(gbo).sdqClient(sdqClient).sdq(sdq).build();
  }
}
