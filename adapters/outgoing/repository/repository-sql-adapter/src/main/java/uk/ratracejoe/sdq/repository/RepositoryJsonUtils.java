package uk.ratracejoe.sdq.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Supplier;
import uk.ratracejoe.sdq.exception.SdqException;

public class RepositoryJsonUtils {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static String whereClause(List<String> conditions) {
    if (conditions.isEmpty()) return "";

    return String.format("WHERE %s", String.join(" AND ", conditions));
  }

  public static <T> T parseJson(String json, TypeReference<T> type, Supplier<T> getDefault) {
    if (json == null) return getDefault.get();
    try {
      return MAPPER.readValue(json, type);
    } catch (Exception e) {
      throw new SdqException("Failed to parse JSON: " + json, e);
    }
  }

  public static <T> T parseJson(String json, Class<T> clazz) {
    try {
      return MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new SdqException("Could not parse progress", e);
    }
  }
}
