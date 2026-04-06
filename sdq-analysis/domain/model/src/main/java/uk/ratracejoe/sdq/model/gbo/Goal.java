package uk.ratracejoe.sdq.model.gbo;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Goal(UUID clientId, UUID goalId, String description) {}
