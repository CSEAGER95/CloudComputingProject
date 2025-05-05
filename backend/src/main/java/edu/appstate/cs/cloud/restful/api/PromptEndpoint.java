package edu.appstate.cs.cloud.restful.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.appstate.cs.cloud.restful.StoryGenerator;
import edu.appstate.cs.cloud.restful.datastore.PromptService;
import edu.appstate.cs.cloud.restful.models.Prompt;
import edu.appstate.cs.cloud.restful.models.Story;

@RestController
@RequestMapping(value = "/prompt")
public class PromptEndpoint {
    @Autowired
    private StoryGenerator storyGenerator;

    @Autowired
    private PromptService promptService;

    @GetMapping("/stories")
    public List<Story> getAllStories() {
        return promptService.getAllStories();
    }
    
    @GetMapping("/prompts")
    public List<Prompt> getAllPrompts() {
        return promptService.getAllPrompts();
    }

    @GetMapping(value = "/init")
    public boolean initCourses() {
        // Create some sample courses
        return true;
    }

    @PostMapping(value = "/story")
public ResponseEntity<?> createStory(@RequestBody Prompt prompt) {
    try {
        // Generate story using StoryGenerator
        String storyText = storyGenerator.generate(prompt.getPrompt());
        
        // Create and save the story entity
        Story story = new Story.Builder()
            .withPrompt(prompt.getPrompt())
            .withStory(storyText)
            .withUpvotes(0)
            .withDownvotes(0)
            .build();
        
        // Save to datastore
        Story savedStory = promptService.saveStory(story);
        return new ResponseEntity<>(savedStory, HttpStatus.CREATED);
    } catch (Exception e) {
        System.err.println("Error creating story: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    
    @PostMapping(value = "/upvote/{storyId}")
    public ResponseEntity<?> upvoteStory(@PathVariable String storyId) {
        try {
            Story story = promptService.upvoteStory(storyId);
            return new ResponseEntity<>(story, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error upvoting story: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/downvote/{storyId}")
    public ResponseEntity<?> downvoteStory(@PathVariable String storyId) {
        try {
            Story story = promptService.downvoteStory(storyId);
            return new ResponseEntity<>(story, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error downvoting story: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}