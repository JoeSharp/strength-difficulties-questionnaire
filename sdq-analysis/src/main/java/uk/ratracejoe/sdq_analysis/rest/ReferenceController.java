package uk.ratracejoe.sdq_analysis.rest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ratracejoe.sdq_analysis.dto.Category;

@RestController
@RequestMapping("/api/reference")
public class ReferenceController {

    @GetMapping
    public Category[] refInfo() {
        return Category.values();
    }
}
