package uk.ratracejoe.sdq.service;

import java.io.IOException;
import java.io.InputStream;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.ParsedFile;

public interface UploadService {
  ParsedFile ingestFile(String filename, InputStream file) throws IOException, SdqException;
}
