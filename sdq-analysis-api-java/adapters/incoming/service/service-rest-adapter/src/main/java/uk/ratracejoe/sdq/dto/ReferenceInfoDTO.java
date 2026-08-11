package uk.ratracejoe.sdq.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import uk.ratracejoe.sdq.model.EnumValue;
import uk.ratracejoe.sdq.model.demographics.DemographicField;
import uk.ratracejoe.sdq.model.sdq.Category;
import uk.ratracejoe.sdq.model.sdq.Posture;
import uk.ratracejoe.sdq.model.sdq.Statement;

@Builder
public record ReferenceInfoDTO(
    List<EnumValue> goalTypes,
    List<Category> categories,
    List<Statement> statements,
    List<Posture> postures,
    Map<DemographicField, List<EnumValue>> demographicFields) {}
