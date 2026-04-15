package uk.ratracejoe.sdq.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDTO(String message, int status) {}
