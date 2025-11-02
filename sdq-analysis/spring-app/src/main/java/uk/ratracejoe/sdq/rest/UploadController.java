package uk.ratracejoe.sdq.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.ratracejoe.sdq.dto.ParsedFile;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.service.UploadService;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {
  private final UploadService uploadService;

  @PostMapping
  public List<ParsedFile> uploadFiles(@RequestParam("sdqFiles") List<MultipartFile> files)
      throws SdqException {
    List<ParsedFile> results = new ArrayList<>();
    try {
      for (MultipartFile file : files) {
        var parsed = uploadService.ingestFile(file.getOriginalFilename(), file.getInputStream());
        results.add(parsed);
      }
    } catch (IOException e) {
      throw new SdqException("Could not parse workbook");
    }

    return results;
  }
}
