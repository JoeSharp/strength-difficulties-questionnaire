package uk.ratracejoe.sdq.xslx;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.apache.poi.ss.usermodel.*;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.model.demographics.*;

public class WorkbookDemographicExtractor {
  public static final String DEMOGRAPHIC_SHEET_NAME = "Demographic Information";
  private static final int ROW_NUMBER_ANSWERS = 1;
  private static final int NUMBER_INTERVENTION_TYPES = 4;

  public SdqClient parse(Workbook workbook, String filename) throws SdqException {
    Sheet sheet =
        Optional.ofNullable(workbook.getSheet(DEMOGRAPHIC_SHEET_NAME))
            .orElseThrow(() -> new SdqException("Could not find demographic information sheet"));

    Row row =
        Optional.ofNullable(sheet.getRow(ROW_NUMBER_ANSWERS))
            .orElseThrow(
                () -> new SdqException("Could not find answers row within demographic sheet"));

    AtomicInteger cellNum = new AtomicInteger(0);
    LocalDate dateOfBirth = readDateCell(row, cellNum.getAndIncrement());
    Gender gender =
        readStringCell(row, cellNum.getAndIncrement(), Gender::fromDisplay, Gender::defaultValue);
    Council council =
        readStringCell(row, cellNum.getAndIncrement(), Council::fromDisplay, Council::defaultValue);
    Ethnicity ethnicity =
        readStringCell(
            row, cellNum.getAndIncrement(), Ethnicity::fromDisplay, Ethnicity::defaultValue);
    EnglishAsAdditionalLanguage eal =
        readStringCell(
            row,
            cellNum.getAndIncrement(),
            EnglishAsAdditionalLanguage::fromDisplay,
            EnglishAsAdditionalLanguage::defaultValue);
    DisabilityStatus disabilityStatus =
        readStringCell(
            row,
            cellNum.getAndIncrement(),
            DisabilityStatus::fromDisplay,
            DisabilityStatus::defaultValue);
    DisabilityType disabilityType =
        readStringCell(
            row,
            cellNum.getAndIncrement(),
            DisabilityType::fromDisplay,
            DisabilityType::defaultValue);
    CareExperience careExperience =
        readStringCell(
            row,
            cellNum.getAndIncrement(),
            CareExperience::fromDisplay,
            CareExperience::defaultValue);
    List<InterventionType> interventionTypes =
        IntStream.range(0, NUMBER_INTERVENTION_TYPES)
            .mapToObj(i -> readStringCell(row, cellNum.getAndIncrement()))
            .map(InterventionType::fromDisplay)
            .toList();
    Integer aces = readIntCell(row, cellNum.getAndIncrement());
    FundingSource fundingSource =
        readStringCell(
            row,
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
        interventionTypes,
        aces,
        fundingSource);
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
