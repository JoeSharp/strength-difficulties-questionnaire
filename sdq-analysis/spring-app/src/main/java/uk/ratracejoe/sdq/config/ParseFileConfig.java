package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.SdqFileParser;
import uk.ratracejoe.sdq.xslx.XslxSdqFileParser;

@Configuration
public class ParseFileConfig {
  @Bean
  public SdqFileParser parseSdqFile() {
    return new XslxSdqFileParser();
  }
}
