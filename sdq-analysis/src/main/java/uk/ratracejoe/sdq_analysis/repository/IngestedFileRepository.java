package uk.ratracejoe.sdq_analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ratracejoe.sdq_analysis.repository.entity.IngestedFile;

import java.util.UUID;

public interface IngestedFileRepository extends JpaRepository<IngestedFile, UUID> {
}
