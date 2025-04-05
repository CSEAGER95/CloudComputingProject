package edu.appstate.cs.cloud.restful.api;

import edu.appstate.cs.cloud.restful.datastore.PromptService;
import edu.appstate.cs.cloud.restful.models.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/prompt")
public class PromptEndpoint {
    @Autowired
    private PromptService PromptService;

    @GetMapping
    public List<Prompt> getAllPrompts() {
        return PromptService.getAllPrompts();
    }

    @GetMapping(value = "/init")
    public boolean initCourses() {
        // Create some sample courses
        return true;
    }
}
