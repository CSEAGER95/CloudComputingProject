package edu.appstate.cs.cloud.restful.api;

import edu.appstate.cs.cloud.restful.datastore.PromptService;
import edu.appstate.cs.cloud.restful.models.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @PostMapping(value = "/story")
    public ResponseEntity<Prompt> addStory(@RequestBody Prompt prompt) {
        try {
            PromptService.createPrompt(prompt);
            return new ResponseEntity<>(prompt, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error adding story: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
