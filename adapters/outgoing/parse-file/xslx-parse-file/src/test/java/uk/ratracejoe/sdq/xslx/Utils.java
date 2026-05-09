package uk.ratracejoe.sdq.xslx;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.EnumValue;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.sdq.Category;
import uk.ratracejoe.sdq.model.sdq.Statement;
import uk.ratracejoe.sdq.service.RefDataService;

public class Utils {
  public static final String XLSX_TEST_FILE = "MasterDataRecordFor28_6.xlsx";

  public static InputStream workbookStream() {
    return WorkbookDemographicExtractor.class.getClassLoader().getResourceAsStream(XLSX_TEST_FILE);
  }

  public static RefDataService refDataService() {
    return new RefDataService() {
      @Override
      public Map<DemographicField, List<EnumValue>> getDemographicOptions() throws SdqException {
        return null;
      }

      @Override
      public List<Statement> getStatements() {
        return List.of();
      }

      @Override
      public List<Category> getCategories() {
        return null;
      }

      @Override
      public Statement getStatement(String key) {
        return null;
      }
    };
  }

  public static WorkbookSdqExtractor sdqExtractor() {
    return new WorkbookSdqExtractor(refDataService());
  }

  private static WorkbookGboExtractor gboExtractor() {
    return new WorkbookGboExtractor();
  }

  private static WorkbookDemographicExtractor demographicExtractor() {
    return new WorkbookDemographicExtractor();
  }

  public static WorkbookClientFileExtractor fileParser() {
    return new WorkbookClientFileExtractor(sdqExtractor(), gboExtractor(), demographicExtractor());
  }
  ;

  public static Workbook workbookLoaded() throws IOException {
    var input = workbookStream();
    assertNotNull(input); // Always good to check!
    return new XSSFWorkbook(input);
  }
}
