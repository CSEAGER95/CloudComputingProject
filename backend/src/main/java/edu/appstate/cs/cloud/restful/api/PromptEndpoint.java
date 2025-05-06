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

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/prompt")
public class PromptEndpoint {
    @Autowired
    private StoryGenerator storyGenerator;

    @Autowired
    private PromptService promptService;

    @GetMapping("/")
    public List<Story> getAllStoriesRoot() {
        return promptService.getAllStories();
    }

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

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Backend is working!");
    }

    @GetMapping("/testdatastore")
    public ResponseEntity<?> testDatastoreConnection() {
        try {
            // Get Datastore connection info
            String datastoreInfo = promptService.getDatastoreInfo();
            
            // Get stories
            List<Story> stories = promptService.getAllStories();
            
            // Build response
            StringBuilder response = new StringBuilder();
            response.append(datastoreInfo).append("\n\n");
            response.append("Stories found: ").append(stories.size()).append("\n");
            
            // Add details about each story if any were found
            if (!stories.isEmpty()) {
                response.append("Story details:\n");
                for (Story story : stories) {
                    response.append("- ID: ").append(story.getId())
                        .append(", Prompt: \"").append(story.getPrompt().substring(0, Math.min(30, story.getPrompt().length()))).append("...\"\n");
                }
            }
            
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Datastore connection failed: " + e.getMessage());
        }
    }

    @GetMapping("/createtest")
    public ResponseEntity<?> createTestEntity() {
        try {
            // Create a test story with fixed ID for easy retrieval
            String testId = "test-" + System.currentTimeMillis();
            Story testStory = new Story.Builder()
                .withId(testId)
                .withPrompt("Test prompt created through API")
                .withStory("This is a test story created through the API.")
                .withUpvotes(0)
                .withDownvotes(0)
                .build();
            
            // Save to datastore
            Story savedStory = promptService.saveStory(testStory);
            
            // Try to immediately read it back
            List<Story> allStories = promptService.getAllStories();
            boolean foundInList = false;
            for (Story story : allStories) {
                if (story.getId().equals(testId)) {
                    foundInList = true;
                    break;
                }
            }
            
            StringBuilder response = new StringBuilder();
            response.append("Test story created with ID: ").append(savedStory.getId()).append("\n");
            response.append("Total stories after creation: ").append(allStories.size()).append("\n");
            response.append("Test story found in list: ").append(foundInList);
            
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create test story: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
        }
    }

    @GetMapping("/debugdatastore")
    public ResponseEntity<?> debugDatastore() {
        try {
            StringBuilder response = new StringBuilder();
            
            // Get connection info
            String datastoreInfo = promptService.getDatastoreInfo();
            response.append(datastoreInfo).append("\n\n");
            
            // Get all kinds in the datastore
            List<String> kinds = promptService.getAllKinds();
            response.append("Entity kinds found: ").append(kinds.size()).append("\n");
            if (!kinds.isEmpty()) {
                response.append("Kinds: ").append(String.join(", ", kinds)).append("\n\n");
            }
            
            // Check if Story kind exists
            boolean storyKindExists = promptService.checkKindExists("Story");
            response.append("Story kind exists: ").append(storyKindExists).append("\n");
            
            // Check if lowercase "story" kind exists
            boolean lowercaseStoryKindExists = promptService.checkKindExists("story");
            response.append("story (lowercase) kind exists: ").append(lowercaseStoryKindExists).append("\n\n");
            
            // Try to get all stories
            List<Story> stories = promptService.getAllStories();
            response.append("Stories found: ").append(stories.size()).append("\n");
            
            // Add details about each story if any were found
            if (!stories.isEmpty()) {
                response.append("Story details:\n");
                for (Story story : stories) {
                    response.append("- ID: ").append(story.getId())
                        .append(", Prompt: \"").append(story.getPrompt().substring(0, Math.min(30, story.getPrompt().length()))).append("...\"\n");
                }
            }
            
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Debug failed: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
        }
    }

    @GetMapping("/entitydetails")
    public ResponseEntity<?> getEntityDetails() {
        try {
            // Use your existing promptService to get the structure information
            String entityDetails = promptService.getEntityStructure();
            return ResponseEntity.ok(entityDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to examine entity structure: " + e.getMessage());
        }
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