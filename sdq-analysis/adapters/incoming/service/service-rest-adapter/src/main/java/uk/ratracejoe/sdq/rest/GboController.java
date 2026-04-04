package uk.ratracejoe.sdq.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq.service.GboService;

@RestController
@RequestMapping("/api/gbo")
@RequiredArgsConstructor
public class GboController {
  private final GboService gboService;
}
