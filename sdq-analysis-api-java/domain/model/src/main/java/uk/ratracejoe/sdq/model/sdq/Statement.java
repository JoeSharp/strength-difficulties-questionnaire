package uk.ratracejoe.sdq.model.sdq;

public record Statement(
    int order, String key, Category category, String description, boolean isTruePositive) {}
