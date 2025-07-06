package uk.ratracejoe.sdq_analysis.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq_analysis.database.repository.DemographicOptionRepository;
import uk.ratracejoe.sdq_analysis.dto.*;
import uk.ratracejoe.sdq_analysis.exception.SdqException;
import uk.ratracejoe.sdq_analysis.service.DatabaseService;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceController {
    private final DatabaseService databaseService;
    private final DemographicOptionRepository demographicOptionRepository;

    private static <E extends Enum<E>> List<Map<String, Object>> describe(Class<E> enumClass) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (E constant : enumClass.getEnumConstants()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", constant.name());

            for (Method method : enumClass.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())
                        && method.getParameterCount() == 0
                        && method.getDeclaringClass() != Enum.class
                        && !method.getName().equals("name")
                        && !method.getName().equals("values")) {
                    try {
                        entry.put(method.getName(), method.invoke(constant));
                    } catch (Exception ignored) {
                    }
                }
            }

            result.add(entry);
        }
        return result;
    }

    @GetMapping
    public ReferenceInfo refInfo() throws SdqException {
        Map<DemographicField, List<String>> demographicOptions;
        if (databaseService.databaseExists()) {
            demographicOptions = demographicOptionRepository.getOptionsByField();
        } else {
            demographicOptions = Collections.emptyMap();
        }
        return new ReferenceInfo(
                describe(Category.class),
                describe(Statement.class),
                describe(Posture.class),
                demographicOptions
        );
    }

}
