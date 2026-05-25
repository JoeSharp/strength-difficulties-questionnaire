package uk.ratracejoe.sdq.xslx;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.apache.poi.ss.usermodel.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.SdqClient;
import uk.ratracejoe.sdq.model.WorkbookFormat;
import uk.ratracejoe.sdq.model.demographics.*;

public class WorkbookDemographicExtractor {
  public static final String DEMOGRAPHIC_SHEET_NAME = "Demographic Information";
  private static final int ROW_INTERVENTION_SESSIONS = 3;
  private static final int NUMBER_INTERVENTION_TYPES = 4;
  private static final int NUMBER_DISABILITY_TYPES = 4;
  private static final int NUMBER_EXTENDED_ACES = 9;

  public SdqClient parse(Workbook workbook, String filename) throws SdqException {
    Sheet sheet =
        Optional.ofNullable(workbook.getSheet(DEMOGRAPHIC_SHEET_NAME))
            .orElseThrow(() -> new SdqException("Could not find demographic information sheet"));
    WorkbookFormat format = getFormat(sheet);
    int answersRowNumber =
        switch (format) {
          case ORIGINAL -> 1;
          case REVISED_MAY_26 -> 2;
        };
    int startingColumnNumber =
        switch (format) {
          case ORIGINAL -> 0;
          case REVISED_MAY_26 -> 1;
        };

    Row answersRow =
        Optional.ofNullable(sheet.getRow(answersRowNumber))
            .orElseThrow(
                () -> new SdqException("Could not find answers row within demographic sheet"));
    Row interventionSessionsRow = sheet.getRow(ROW_INTERVENTION_SESSIONS);

    AtomicInteger cellNum = new AtomicInteger(startingColumnNumber);
    LocalDate dateOfBirth = readDateCell(answersRow, cellNum.getAndIncrement());
    Gender gender =
        readStringCell(
            answersRow, cellNum.getAndIncrement(), Gender::fromDisplay, Gender::defaultValue);
    Council council =
        readStringCell(
            answersRow, cellNum.getAndIncrement(), Council::fromDisplay, Council::defaultValue);
    Ethnicity ethnicity =
        readStringCell(
            answersRow, cellNum.getAndIncrement(), Ethnicity::fromDisplay, Ethnicity::defaultValue);
    EnglishAsAdditionalLanguage eal =
        readStringCell(
            answersRow,
            cellNum.getAndIncrement(),
            EnglishAsAdditionalLanguage::fromDisplay,
            EnglishAsAdditionalLanguage::defaultValue);
    DisabilityStatus disabilityStatus =
        readStringCell(
            answersRow,
            cellNum.getAndIncrement(),
            DisabilityStatus::fromDisplay,
            DisabilityStatus::defaultValue);
    int numberDisabilityTypes =
        switch (format) {
          case ORIGINAL -> 1;
          case REVISED_MAY_26 -> NUMBER_DISABILITY_TYPES;
        };
    List<DisabilityType> disabilityType =
        IntStream.range(0, numberDisabilityTypes)
            .mapToObj(
                i ->
                    readStringCell(
                        answersRow,
                        cellNum.getAndIncrement(),
                        DisabilityType::fromDisplay,
                        DisabilityType::defaultValue))
            .filter(Predicate.not(DisabilityType.NOT_APPLICABLE::equals))
            .toList();

    CareExperience careExperience =
        readStringCell(
            answersRow,
            cellNum.getAndIncrement(),
            CareExperience::fromDisplay,
            CareExperience::defaultValue);
    List<Intervention> interventions =
        IntStream.range(0, NUMBER_INTERVENTION_TYPES)
            .mapToObj(
                i -> {
                  int cellNumber = cellNum.getAndIncrement();
                  String typeStr = readStringCell(answersRow, cellNumber);
                  InterventionType type = InterventionType.fromDisplay(typeStr);
                  int sessions =
                      switch (format) {
                        case ORIGINAL -> 0;
                        case REVISED_MAY_26 -> readIntCell(interventionSessionsRow, cellNumber);
                      };
                  return new Intervention(type, sessions);
                })
            .filter(i -> !InterventionType.UKKNOWN.equals(i.type()))
            .toList();
    Integer aces = readIntCell(answersRow, cellNum.getAndIncrement());
    if (WorkbookFormat.REVISED_MAY_26.equals(format)) {
      for (int i = 0; i < NUMBER_EXTENDED_ACES; i++) {
        cellNum.incrementAndGet();
      }
    }
    FundingSource fundingSource =
        readStringCell(
            answersRow,
            cellNum.getAndIncrement(),
            FundingSource::fromDisplay,
            FundingSource::defaultValue);

    return new SdqClient(
        UUID.randomUUID(),
        filename,
        dateOfBirth,
        gender,
        council,
        ethnicity,
        eal,
        disabilityStatus,
        disabilityType,
        careExperience,
        interventions,
        aces,
        fundingSource);
  }

  private WorkbookFormat getFormat(Sheet demographicSheet) {
    return Optional.ofNullable(demographicSheet.getRow(3))
        .map(r -> r.getCell(0))
        .map(Cell::getStringCellValue)
        .map(s -> s.contains("Number of sessions"))
        .filter(s -> s)
        .map(s -> WorkbookFormat.REVISED_MAY_26)
        .orElse(WorkbookFormat.ORIGINAL);
  }

  private LocalDate readDateCell(Row row, int cellNum) {
    Cell cell = row.getCell(cellNum);
    if (cell == null || cell.getCellType() == CellType.BLANK) {
      return null;
    }

    Date date = cell.getDateCellValue();

    return date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
  }

  private String readStringCell(Row row, int cellNum) {
    return Optional.ofNullable(row.getCell(cellNum)).map(Cell::getStringCellValue).orElse("");
  }

  private <T> T readStringCell(
      Row row, int cellNum, Function<String, T> converter, Supplier<T> getDefault) {
    return Optional.ofNullable(row.getCell(cellNum))
        .map(Cell::getStringCellValue)
        .map(converter)
        .orElseGet(getDefault);
  }

  private Integer readIntCell(Row row, int cellNum) {
    return Optional.ofNullable(row.getCell(cellNum))
        .map(Cell::getNumericCellValue)
        .map(Double::intValue)
        .orElseGet(() -> 0);
  }
}
