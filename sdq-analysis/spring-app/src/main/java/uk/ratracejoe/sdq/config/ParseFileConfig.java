package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ratracejoe.sdq.ClientFileParser;
import uk.ratracejoe.sdq.xslx.XslxClientFileParser;

@Configuration
public class ParseFileConfig {
  @Bean
  public ClientFileParser parseSdqFile() {
    return new XslxClientFileParser();
  }
}
