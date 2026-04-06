package uk.ratracejoe.sdq;

import java.io.InputStream;
import uk.ratracejoe.sdq.xslx.WorkbookDemographicExtractor;

public class Utils {
  public static final String XLSX_TEST_FILE = "Test File 1.xlsx";

  public static InputStream workbookStream() {
    return WorkbookDemographicExtractor.class.getClassLoader().getResourceAsStream(XLSX_TEST_FILE);
  }
}
