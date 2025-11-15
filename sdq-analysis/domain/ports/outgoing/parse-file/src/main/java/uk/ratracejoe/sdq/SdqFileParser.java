package uk.ratracejoe.sdq;

import java.io.InputStream;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ParsedFile;

public interface SdqFileParser {
  ParsedFile parse(String filename, InputStream file) throws SdqException;
}
