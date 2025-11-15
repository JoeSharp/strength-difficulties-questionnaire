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
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.*;
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

  @GetMapping("/demographic_report/{demographic}")
  public DemographicReport getDemographicReport(@PathVariable("demographic") String demographic) {
    return fileService.getDemographicReport(demographic);
  }

  @GetMapping("/sdq/{fileId}")
  public Map<Assessor, List<SdqScore>> getSdq(@PathVariable("fileId") UUID fileId)
      throws SdqException {
    return sdqService.getScores(fileId);
  }

  @GetMapping("/gbo/{fileId}")
  public Map<Assessor, List<GboScore>> getGbo(@PathVariable("fileId") UUID fileUuid)
      throws SdqException {
    return gboService.getGbo(fileUuid);
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<ClientFile> getByUUID(@PathVariable("fileId") UUID uuid)
      throws SdqException {
    return fileService
        .getByUUID(uuid)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
