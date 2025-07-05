package uk.ratracejoe.sdq_analysis.service.xslx;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.DemographicField;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import java.util.*;

import static uk.ratracejoe.sdq_analysis.service.xslx.XslxDemographicExtractor.DEMOGRAPHIC_SHEET_NAME;

@Service
public class XslxStructureExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(XslxStructureExtractor.class);

    public Map<DemographicField, List<String>> extractDemographicOptions(Workbook workbook) throws SdqException {
        Map<DemographicField, List<String>> optionsByField = new EnumMap<>(DemographicField.class);
        Sheet sheet = Optional.ofNullable(workbook.getSheet(DEMOGRAPHIC_SHEET_NAME))
                .orElseThrow(() -> new SdqException("Could not find demographic sheet"));
        Row headerRow = Optional.ofNullable(sheet.getRow(0))
                .orElseThrow(() -> new SdqException("Could not extract demographic header row"));
        Row dataRow = Optional.ofNullable(sheet.getRow(1))
                .orElseThrow(() -> new SdqException("Could not extract demographic data row"));

        List<? extends DataValidation> validations = sheet.getDataValidations();

        headerRow.cellIterator().forEachRemaining(header -> {
            DemographicField field = DemographicField.fromHeading(header.getStringCellValue());
            int col = header.getAddress().getColumn();
            Cell dataCell = dataRow.getCell(col);
            List<String> options = getOptions(validations, field, dataCell);
            optionsByField.put(field, options);
        });

        return optionsByField;
    }

    private List<String> getOptions(List<? extends DataValidation> validations,
                                    DemographicField field,
                                    Cell cell) {
        for (DataValidation validation : validations) {
            for (CellRangeAddress range : validation.getRegions().getCellRangeAddresses()) {
                if (range.isInRange(cell)) {
                    DataValidationConstraint constraint = validation.getValidationConstraint();
                    if (constraint.getValidationType() == DataValidationConstraint.ValidationType.LIST) {
                        String[] options = constraint.getExplicitListValues();
                        return Arrays.asList(options);
                    }
                }
            }
        }

        LOGGER.warn("Could not find options for field {}", field);
        return Collections.emptyList();
    }
}
