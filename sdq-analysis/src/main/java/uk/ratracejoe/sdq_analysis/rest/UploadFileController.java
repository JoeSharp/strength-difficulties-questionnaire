package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq_analysis.repository.IngestedFileRepository;
import uk.ratracejoe.sdq_analysis.repository.entity.IngestedFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadFileController {
    private final IngestedFileRepository fileRepository;

    @GetMapping
    public List<String> ingestedFilenames() {
        return fileRepository
                .findAll().stream()
                .map(IngestedFile::getFilename)
                .toList();
    }

    @PostMapping
    public String ingestFile() {
        var newFile = new IngestedFile(UUID.randomUUID(),"foo");
        fileRepository.save(newFile);
        return "hello";
    }
}
