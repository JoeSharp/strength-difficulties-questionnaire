package uk.ratracejoe.sdq.entity;

import java.util.UUID;
import uk.ratracejoe.sdq.model.InterventionType;

public record InterventionTypeEntity(UUID fileId, InterventionType interventionType) {}
