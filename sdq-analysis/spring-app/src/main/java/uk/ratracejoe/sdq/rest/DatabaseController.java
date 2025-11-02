package uk.ratracejoe.sdq.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ratracejoe.sdq.service.DatabaseService;

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
