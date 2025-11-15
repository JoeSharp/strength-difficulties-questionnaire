package uk.ratracejoe.sdq.xslx;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Utils {
  public static final String XLSX_TEST_FILE = "MasterDataRecordFor28_6.xlsx";

  public static InputStream workbookStream() {
    return XslxDemographicExtractor.class.getClassLoader().getResourceAsStream(XLSX_TEST_FILE);
  }

  public static Workbook workbookLoaded() throws IOException {
    var input = workbookStream();
    assertNotNull(input); // Always good to check!
    return new XSSFWorkbook(input);
  }
}
