package uk.ratracejoe.sdq.xslx;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ClientFile;

public class XslxDemographicExtractor {
  public static final String DEMOGRAPHIC_SHEET_NAME = "Demographic Information";
  private static final int ROW_NUMBER_ANSWERS = 1;
  private static final int NUMBER_INTERVENTION_TYPES = 4;

  public ClientFile parse(Workbook workbook, String filename) throws SdqException {
    Sheet sheet =
        Optional.ofNullable(workbook.getSheet(DEMOGRAPHIC_SHEET_NAME))
            .orElseThrow(() -> new SdqException("Could not find demographic information sheet"));

    Row row =
        Optional.ofNullable(sheet.getRow(ROW_NUMBER_ANSWERS))
            .orElseThrow(
                () -> new SdqException("Could not find answers row within demographic sheet"));

    AtomicInteger cellNum = new AtomicInteger(0);
    Instant dateOfBirth = readDateCell(row, cellNum.getAndIncrement());
    String gender = readStringCell(row, cellNum.getAndIncrement());
    String council = readStringCell(row, cellNum.getAndIncrement());
    String ethnicity = readStringCell(row, cellNum.getAndIncrement());
    String eal = readStringCell(row, cellNum.getAndIncrement());
    String disabilityStatus = readStringCell(row, cellNum.getAndIncrement());
    String disabilityType = readStringCell(row, cellNum.getAndIncrement());
    String careExperience = readStringCell(row, cellNum.getAndIncrement());
    List<String> interventionTypes =
        IntStream.range(0, NUMBER_INTERVENTION_TYPES)
            .mapToObj(i -> readStringCell(row, cellNum.getAndIncrement()))
            .toList();
    Integer aces = readIntCell(row, cellNum.getAndIncrement());
    String fundingSource = readStringCell(row, cellNum.getAndIncrement());

    return new ClientFile(
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

  private Instant readDateCell(Row row, int cellNum) {
    return Optional.ofNullable(row.getCell(cellNum))
        .map(Cell::getDateCellValue)
        .map(Date::toInstant)
        .orElse(null);
  }

  private String readStringCell(Row row, int cellNum) {
    return Optional.ofNullable(row.getCell(cellNum)).map(Cell::getStringCellValue).orElse("");
  }

  private Integer readIntCell(Row row, int cellNum) {
    return Optional.ofNullable(row.getCell(cellNum))
        .map(Cell::getNumericCellValue)
        .map(Double::intValue)
        .orElseGet(() -> 0);
  }
}
