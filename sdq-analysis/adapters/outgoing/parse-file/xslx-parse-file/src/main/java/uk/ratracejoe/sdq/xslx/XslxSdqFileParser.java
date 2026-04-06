package uk.ratracejoe.sdq.xslx;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
import uk.ratracejoe.sdq.model.sdq.SdqReportingPeriod;

public class XslxSdqFileParser implements SdqFileParser {
  private final XslxSdqExtractor xslSdqExtractor;
  private final XslxGboExtractor xslxGboExtractor;
  private final XslxDemographicExtractor xslDemographicExtractor;

  public XslxSdqFileParser() {
    xslSdqExtractor = new XslxSdqExtractor();
    xslxGboExtractor = new XslxGboExtractor();
    xslDemographicExtractor = new XslxDemographicExtractor();
  }

  @Override
  public ParsedFile parse(String filename, InputStream file) throws SdqException {
    try {
      Workbook workbook = new XSSFWorkbook(file);

      SdqClient sdqClient = xslDemographicExtractor.parse(workbook, filename);
      List<SdqReportingPeriod> sdq = xslSdqExtractor.parse(sdqClient.clientId(), workbook);
      List<GboParsedPeriod> gboPeriods = xslxGboExtractor.parse(workbook);

      return ParsedFile.builder().sdqClient(sdqClient).sdq(sdq).build();
    } catch (IOException e) {
      throw new SdqException("Could not parse XLSL Workbook", e);
    }
  }
}
