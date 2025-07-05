package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq_analysis.dto.ClientFile;
import uk.ratracejoe.sdq_analysis.dto.SdqScoresSummary;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.ClientFileService;
import uk.ratracejoe.sdq_analysis.service.SdqResponseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ClientFileController {
    private final ClientFileService fileService;
    private final SdqResponseService sdqResponseService;

    @GetMapping
    public List<ClientFile> getAll() throws SdqException {
        return fileService.getAll();
    }

    @GetMapping("/scores")
    public List<SdqScoresSummary> getScores() {
        return sdqResponseService.getScores();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ClientFile> getByUUID(@PathVariable("uuid") UUID uuid) throws SdqException {
        return fileService
                .getByUUID(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}