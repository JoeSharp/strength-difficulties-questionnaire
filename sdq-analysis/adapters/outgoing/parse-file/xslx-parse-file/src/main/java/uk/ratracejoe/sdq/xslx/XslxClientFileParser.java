package uk.ratracejoe.sdq.xslx;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.ratracejoe.sdq.ClientFileParser;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;

public class XslxClientFileParser implements ClientFileParser {
  private final WorkbookClientFileExtractor clientFileExtractor;

  public XslxClientFileParser() {
    clientFileExtractor = new WorkbookClientFileExtractor();
  }

  @Override
  public ParsedFile parse(String filename, InputStream file) throws SdqException {
    try {
      Workbook workbook = new XSSFWorkbook(file);
      return clientFileExtractor.extract(filename, workbook);
    } catch (IOException e) {
      throw new SdqException("Could not parse XLSL Workbook", e);
    }
  }
}
