package edu.appstate.cs.cloud.restful.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;


@RestController
@RequestMapping(value = "/prompt")
public class PromptEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(PromptEndpoint.class);
    
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
            logger.error("Datastore connection failed", e);
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
            logger.error("Failed to create test story", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create test story: " + e.getMessage() + "\n" + e.getStackTrace()[0]);
        }
    }

    @PostMapping(value = "/story")
    public ResponseEntity<?> createStory(@RequestBody Prompt prompt) {
        logger.info("Received request to create story with prompt: {}", 
                   prompt.getPrompt().substring(0, Math.min(50, prompt.getPrompt().length())));
        
        try {
            if (prompt.getPrompt() == null || prompt.getPrompt().trim().isEmpty()) {
                return new ResponseEntity<>("Prompt cannot be empty", HttpStatus.BAD_REQUEST);
            }
            
            // Generate story using StoryGenerator with better error handling
            String storyText;
            try {
                storyText = storyGenerator.generate(prompt.getPrompt());
                logger.info("Generated story successfully");
            } catch (Exception e) {
                logger.error("Error generating story with AI: {}", e.getMessage(), e);
                // Use fallback story
                storyText = "Unable to generate AI story. Here's a placeholder: Once upon a time, there was a prompt about " + prompt.getPrompt();
            }
            
            // Create and save the story entity
            Story story = new Story.Builder()
                .withPrompt(prompt.getPrompt())
                .withStory(storyText)
                .withUpvotes(0)
                .withDownvotes(0)
                .build();
            
            // Save to datastore
            try {
                logger.info("Saving story to datastore");
                Story savedStory = promptService.saveStoryWithLogging(story);
                logger.info("Story saved with ID: {}", savedStory.getId());
                return new ResponseEntity<>(savedStory, HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error("Failed to save to datastore: {}", e.getMessage(), e);
                return new ResponseEntity<>("Story was generated but couldn't be saved: " + e.getMessage(), 
                                          HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("Unexpected error creating story", e);
            return new ResponseEntity<>("Error creating story: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/upvote/{storyId}")
    public ResponseEntity<?> upvoteStory(@PathVariable String storyId) {
        try {
            Story story = promptService.upvoteStory(storyId);
            return new ResponseEntity<>(story, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error upvoting story: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/downvote/{storyId}")
    public ResponseEntity<?> downvoteStory(@PathVariable String storyId) {
        try {
            Story story = promptService.downvoteStory(storyId);
            return new ResponseEntity<>(story, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error downvoting story: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/datastoretest")
public ResponseEntity<String> testDatastore() {
    try {
        // Use existing methods instead of direct field access
        String datastoreInfo = promptService.getDatastoreInfo();
        
        // Create a test story
        String testId = "test-" + System.currentTimeMillis();
        Story testStory = new Story.Builder()
            .withId(testId)
            .withPrompt("Test prompt for debugging")
            .withStory("This is a test story created for debugging.")
            .withUpvotes(0)
            .withDownvotes(0)
            .build();
        
        // Save using existing method
        Story savedStory = promptService.saveStory(testStory);
        
        return ResponseEntity.ok("Datastore test:\n" +
                               "Connection info: " + datastoreInfo + "\n" +
                               "Test save successful with ID: " + savedStory.getId());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Datastore test failed: " + e.getMessage());
    }
}

@GetMapping("/entitydetails") 
public ResponseEntity<String> entityDetails() {
    try {
        // Get structure info through the service method
        String entityStructure = promptService.getEntityStructure();
        return ResponseEntity.ok(entityStructure);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to get entity structure: " + e.getMessage());
    }
}
@GetMapping("/testvertex")
public ResponseEntity<String> testVertexAI() {
    try {
        // Log details about the environment
        logger.info("Testing Vertex AI connection...");
        logger.info("Project ID: teamprojectmccewenseager");
        logger.info("Region: us-east1");
        
        // Initialize Vertex AI
        VertexAI vertexAi = new VertexAI("teamprojectmccewenseager", "us-east1");
        logger.info("VertexAI initialized successfully");
        
        // Create a model with minimal capabilities
        GenerativeModel model = new GenerativeModel("gemini-1.0-pro", vertexAi);
        logger.info("GenerativeModel created successfully");
        
        // Test with minimal prompt
        GenerateContentResponse response = model.generateContent("Hello world");
        String result = ResponseHandler.getText(response);
        logger.info("Successfully received response from Vertex AI");
        
        return ResponseEntity.ok("Vertex AI authentication successful!\nResponse: " + result);
    } catch (Exception e) {
        logger.error("Vertex AI authentication error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Authentication error: " + e.getMessage() + "\n\nStack trace: " + 
                  Arrays.toString(e.getStackTrace()).substring(0, 500));
    }
}
}