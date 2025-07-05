package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ratracejoe.sdq_analysis.database.repository.ClientFileRepository;
import uk.ratracejoe.sdq_analysis.database.repository.SdqResponseRepository;
import uk.ratracejoe.sdq_analysis.dto.DatabaseStructure;
import uk.ratracejoe.sdq_analysis.dto.DeleteAllResponse;
import uk.ratracejoe.sdq_analysis.dto.ErrorResponse;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.DatabaseService;

import java.io.IOException;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {
    private final DatabaseService databaseService;

    @GetMapping
    public ResponseEntity<Void> databaseExists() {
        return databaseService.databaseExists() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @PostMapping
    public DatabaseStructure createDatabase(@RequestParam("sdqFile") MultipartFile file) throws SdqException, IOException {
        if (databaseService.databaseExists()) {
            throw new SdqException("Database already exists");
        }

        return databaseService.createDatabase(file.getInputStream());
    }

    @DeleteMapping
    public DeleteAllResponse clearDatabase() throws SdqException {
        if (!databaseService.databaseExists()) {
            throw new SdqException("Database does not exist");
        }

        return databaseService.clearDatabase();
    }
}