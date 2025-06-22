package uk.ratracejoe.sdq_analysis.repository.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity( name = "ingested_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngestedFile {
    @Id
    private UUID id;

    private String filename;
}
