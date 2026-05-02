package uk.ratracejoe.sdq.rest;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq.dto.ReferenceInfoDTO;
import uk.ratracejoe.sdq.exception.SdqException;
import uk.ratracejoe.sdq.model.sdq.Posture;
import uk.ratracejoe.sdq.service.RefDataService;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceController {
  private final RefDataService refDataService;

  @GetMapping
  public ReferenceInfoDTO refInfo() throws SdqException {
    return ReferenceInfoDTO.builder()
        .categories(refDataService.getCategories())
        .statements(refDataService.getStatements())
        .postures(Arrays.stream(Posture.values()).toList())
        .demographicFields(refDataService.getDemographicOptions())
        .build();
  }
}
