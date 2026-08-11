package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.ClientFileParser;
import uk.ratracejoe.sdq.service.RefDataService;
import uk.ratracejoe.sdq.xslx.*;

@Configuration
public class ParseFileConfig {
  @Bean
  public WorkbookSdqExtractor sdqExtractor(RefDataService refDataService) {
    return new WorkbookSdqExtractor(refDataService);
  }

  @Bean
  public WorkbookGboExtractor gboExtractor() {
    return new WorkbookGboExtractor();
  }

  @Bean
  public WorkbookDemographicExtractor demographicExtractor() {
    return new WorkbookDemographicExtractor();
  }

  @Bean
  public WorkbookClientFileExtractor clientFileExtractor(
      WorkbookSdqExtractor xslSdqExtractor,
      WorkbookGboExtractor workbookGboExtractor,
      WorkbookDemographicExtractor xslDemographicExtractor) {

    return new WorkbookClientFileExtractor(
        xslSdqExtractor, workbookGboExtractor, xslDemographicExtractor);
  }

  @Bean
  public ClientFileParser parseSdqFile(WorkbookClientFileExtractor clientFileExtractor) {
    return new XslxClientFileParser(clientFileExtractor);
  }
}
