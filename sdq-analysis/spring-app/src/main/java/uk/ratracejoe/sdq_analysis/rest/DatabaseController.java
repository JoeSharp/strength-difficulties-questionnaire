package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq_analysis.service.DatabaseService;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {
  private final DatabaseService databaseService;

  @GetMapping
  public ResponseEntity<Void> databaseExists() {
    return databaseService.databaseExists()
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
