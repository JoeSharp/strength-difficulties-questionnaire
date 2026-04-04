package uk.ratracejoe.sdq.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq.service.SdqService;

@RestController
@RequestMapping("/api/sdq")
@RequiredArgsConstructor
public class SdqController {
  private final SdqService sdqService;
}
