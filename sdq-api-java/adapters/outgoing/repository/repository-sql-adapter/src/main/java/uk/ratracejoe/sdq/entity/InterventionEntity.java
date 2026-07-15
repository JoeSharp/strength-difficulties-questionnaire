package uk.ratracejoe.sdq.entity;

import java.util.UUID;
import uk.ratracejoe.sdq.model.demographics.InterventionType;

public record InterventionEntity(UUID clientId, InterventionType interventionType) {}
