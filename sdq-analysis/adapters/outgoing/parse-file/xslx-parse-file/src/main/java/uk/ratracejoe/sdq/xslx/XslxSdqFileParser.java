package uk.ratracejoe.sdq.xslx;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;

public class XslxSdqFileParser implements SdqFileParser {
  private final XslxSdqExtractor xslSdqExtractor;
  private final XslxGboExtractor xslxGboExtractor;
  private final XslxStructureExtractor structureExtractor;
  private final XslxDemographicExtractor xslDemographicExtractor;

  public XslxSdqFileParser() {
    xslSdqExtractor = new XslxSdqExtractor();
    xslxGboExtractor = new XslxGboExtractor();
    structureExtractor = new XslxStructureExtractor();
    xslDemographicExtractor = new XslxDemographicExtractor();
  }

  @Override
  public ParsedFile parse(String filename, InputStream file) throws SdqException {
    try {
      Workbook workbook = new XSSFWorkbook(file);
      var demographics = structureExtractor.extractDemographicOptions(workbook);
      SdqEnumerations structure = new SdqEnumerations(demographics);

      ClientFile clientFile = xslDemographicExtractor.parse(workbook, filename);
      List<SdqScore> sdq = xslSdqExtractor.parse(clientFile.fileId(), workbook);
      List<GboScore> gbo = xslxGboExtractor.parse(clientFile.fileId(), workbook);

      return new ParsedFile(clientFile, sdq, gbo, structure);
    } catch (IOException e) {
      throw new SdqException("Could not parse XLSL Workbook", e);
    }
  }
}
