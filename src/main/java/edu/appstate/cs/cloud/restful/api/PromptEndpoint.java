package edu.appstate.cs.cloud.restful.api;

import edu.appstate.cs.cloud.restful.datastore.PromptService;
import edu.appstate.cs.cloud.restful.models.Story;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/prompt")
public class PromptEndpoint {
    @Autowired
    private PromptService PromptService;

    @GetMapping
    public ResponseEntity<?> getAllPrompts() {
        try {
            List<Story> stories = PromptService.getAllStories();
            return new ResponseEntity<>(stories, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error retrieving stories: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/story")
    public ResponseEntity<?> addStory(@RequestBody Map<String, String> requestBody) {
        try {
            // Extract prompt from the request body
            String prompt = requestBody.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return new ResponseEntity<>("Prompt cannot be empty", HttpStatus.BAD_REQUEST);
            }
            
            Story story = PromptService.createStory(prompt);
            return new ResponseEntity<>(story, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error adding story: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/upvote/{storyId}")
    public ResponseEntity<?> upvoteStory(@PathVariable String storyId) {
        try {
            Story story = PromptService.upvoteStory(storyId);
            return new ResponseEntity<>(story, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error upvoting story: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/downvote/{storyId}")
    public ResponseEntity<?> downvoteStory(@PathVariable String storyId) {
        try {
            Story story = PromptService.downvoteStory(storyId);
            return new ResponseEntity<>(story, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error downvoting story: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}