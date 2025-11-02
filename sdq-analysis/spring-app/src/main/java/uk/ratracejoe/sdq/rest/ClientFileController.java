package uk.ratracejoe.sdq.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq.dto.Assessor;
import uk.ratracejoe.sdq.dto.ClientFile;
import uk.ratracejoe.sdq.dto.GboSummary;
import uk.ratracejoe.sdq.dto.SdqSummary;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.service.ClientFileService;
import uk.ratracejoe.sdq.service.GboService;
import uk.ratracejoe.sdq.service.SdqService;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientFileController {
  private final ClientFileService fileService;
  private final SdqService sdqService;
  private final GboService gboService;

  @GetMapping
  public List<ClientFile> getAll() throws SdqException {
    return fileService.getAll();
  }

  @GetMapping("/sdq/{uuid}")
  public Map<Assessor, List<SdqSummary>> getSdq(@PathVariable("uuid") UUID fileUuid)
      throws SdqException {
    return sdqService.getScores(fileUuid);
  }

  @GetMapping("/gbo/{uuid}")
  public Map<Assessor, List<GboSummary>> getGbo(@PathVariable("uuid") UUID fileUuid)
      throws SdqException {
    return gboService.getGbo(fileUuid);
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<ClientFile> getByUUID(@PathVariable("uuid") UUID uuid) throws SdqException {
    return fileService
        .getByUUID(uuid)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
