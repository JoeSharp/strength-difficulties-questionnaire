package uk.ratracejoe.sdq.entity;

import java.util.UUID;
import uk.ratracejoe.sdq.model.demographics.DisabilityType;

public record DisabilityTypeEntity(UUID clientId, DisabilityType disabilityType) {}
