package uk.ratracejoe.sdq.xslx;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.ratracejoe.sdq.repository.StatementRepository;
import uk.ratracejoe.sdq.repository.StatementRepositoryImpl;
import uk.ratracejoe.sdq.service.RefDataService;
import uk.ratracejoe.sdq.service.RefDataServiceImpl;

public class Utils {
  public static final String XLSX_TEST_FILE_1 = "MasterDataRecordFor28_6.xlsx";
  public static final String XLSX_TEST_FILE_REVISED =
      "Master Data Record for 09.05.26 Revised.xlsx";

  public static InputStream workbookStream(String filename) {
    return WorkbookDemographicExtractor.class.getClassLoader().getResourceAsStream(filename);
  }

  public static RefDataService refDataService() {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setServerNames(new String[] {"localhost"});
    dataSource.setPortNumbers(new int[] {5435});
    dataSource.setDatabaseName("sdq");
    dataSource.setUser("test");
    dataSource.setPassword("testPassword123");

    JdbcClient jdbcClient = JdbcClient.create(dataSource);
    StatementRepository statementRepository = new StatementRepositoryImpl(jdbcClient);
    return new RefDataServiceImpl(statementRepository);
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

  public static Workbook workbookLoaded(String filename) throws IOException {
    var input = workbookStream(filename);
    assertNotNull(input); // Always good to check!
    return new XSSFWorkbook(input);
  }
}
