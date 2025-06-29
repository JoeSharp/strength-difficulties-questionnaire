package uk.ratracejoe.sdq_analysis.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.*;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class XslDemographicExtractor {
    private static final String SHEET_NAME = "Demographic Information";
    private static final int ROW_NUMBER_ANSWERS = 1;

    public UploadFile parse(Workbook workbook, String filename) throws SdqException {
        Sheet sheet = Optional.ofNullable(workbook.getSheet(SHEET_NAME))
                .orElseThrow(() -> new SdqException("Could not find demographic information sheet"));

        Row row = Optional.ofNullable(sheet.getRow(ROW_NUMBER_ANSWERS))
                .orElseThrow(() -> new SdqException("Could not find answers row within demographic sheet"));

        String dateOfBirth = getDob(row);
        Gender gender = readCell(row, 1, Gender::fromDisplay);
        Ethnicity ethnicity = readCell(row, 2, Ethnicity::fromDisplay);
        YesNoAbstain eal = readCell(row, 3, YesNoAbstain::fromDisplay);
        YesNoAbstain disabilityStatus = readCell(row, 4, YesNoAbstain::fromDisplay);
        DisabilityType disabilityType = readCell(row, 5, DisabilityType::fromDisplay);
        CareExperience careExperience = readCell(row, 6, CareExperience::fromDisplay);
        InterventionType interventionType = readCell(row, 7, InterventionType::fromDisplay);
        Aces aces = readCell(row, 8, Aces::fromDisplay);
        FundingSource fundingSource = readCell(row, 9, FundingSource::fromDisplay);

        return new UploadFile(UUID.randomUUID(),
                filename,
                dateOfBirth,
                gender,
                ethnicity,
                eal,
                disabilityStatus,
                disabilityType,
                careExperience,
                interventionType,
                aces,
                fundingSource);
    }

    private String getDob(Row row) {
        return Optional.ofNullable(row.getCell(0))
                .map(Cell::getDateCellValue)
                .map(Objects::toString)
                .orElse(null);
    }

    private <T> T readCell(Row row, int cellNum, Function<String, T> parse) {
        return Optional.ofNullable(row.getCell(cellNum))
                .map(Cell::getStringCellValue)
                .map(parse)
                .orElseGet(() -> parse.apply(null));
    }
}
