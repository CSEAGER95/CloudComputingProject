// Update PromptService.java to use Google Datastore
package edu.appstate.cs.cloud.restful.datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.datastore.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.appstate.cs.cloud.restful.models.Prompt;
import edu.appstate.cs.cloud.restful.models.Story;

import java.util.Set;
import java.util.HashSet;
import com.google.cloud.datastore.Key;

@Service
public class PromptService {
    private final Datastore datastore;
    private final KeyFactory storyKeyFactory;

    public PromptService() {
        // Initialize datastore and factory once
        this.datastore = initializeDatastore();
        this.storyKeyFactory = datastore.newKeyFactory().setKind("story");
    }
    
    private Datastore initializeDatastore() {
        try {
            Datastore ds = DatastoreOptions.newBuilder()
                .setProjectId("teamprojectmccewenseager")
                .build()
                .getService();
                
            // Test connection
            Query<Key> testQuery = Query.newKeyQueryBuilder().setKind("story").setLimit(1).build();
            ds.run(testQuery);
            
            logger.info("Successfully connected to Datastore");
            return ds;
        } catch (Exception e) {
            logger.error("Failed to initialize Datastore: {}", e.getMessage(), e);
            return DatastoreOptions.getDefaultInstance().getService();
        }
    }

    public List<Story> getAllStories() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("story")  // Use lowercase "story"
                .build();
        
        QueryResults<Entity> results = datastore.run(query);
        List<Story> stories = new ArrayList<>();
        
        while (results.hasNext()) {
            try {
                Entity entity = results.next();
                
                // Get the ID from the key (either name or ID)
                String id = entity.getKey().getName() != null ? 
                            entity.getKey().getName() : 
                            String.valueOf(entity.getKey().getId());
                
                // Set default values for properties that might be missing
                long upvotes = 0;
                long downvotes = 0;
                
                // Check if properties exist before trying to access them
                if (entity.contains("upvotes")) {
                    upvotes = entity.getLong("upvotes");
                }
                if (entity.contains("downvotes")) {
                    downvotes = entity.getLong("downvotes");
                }
                
                Story story = new Story.Builder()
                        .withId(id)
                        .withPrompt(entity.getString("prompt"))
                        .withStory(entity.getString("story"))
                        .withUpvotes(upvotes)
                        .withDownvotes(downvotes)
                        .build();
                
                stories.add(story);
            } catch (Exception e) {
                // Log the error but continue processing other entities
                System.err.println("Error processing entity: " + e.getMessage());
            }
        }
        
        return stories;
    }

    public List<Prompt> getAllPrompts() {
        // Similar implementation for prompts if needed
        return new ArrayList<>();
    }

    public Story saveStory(Story story) {
        // Generate a unique ID if one doesn't exist
        String id = (story.getId() == null || story.getId().isEmpty()) 
                ? UUID.randomUUID().toString() 
                : story.getId();
        
        Key key = storyKeyFactory.newKey(id);
        
        Entity entity = Entity.newBuilder(key)
                .set("prompt", story.getPrompt())
                .set("story", story.getStory())
                .set("upvotes", story.getUpvotes())
                .set("downvotes", story.getDownvotes())
                .build();
        
        datastore.put(entity);
        
        // Update the ID in the story object
        story.setId(id);
        return story;
    }

    public Story upvoteStory(String storyId) {
        Key key = storyKeyFactory.newKey(storyId);
        Entity entity = datastore.get(key);
        
        if (entity == null) {
            throw new RuntimeException("Story not found");
        }
        
        // Get current upvotes or default to 0 if not present
        long upvotes = entity.contains("upvotes") ? entity.getLong("upvotes") + 1 : 1;
        
        // Create a new entity builder from the existing entity
        Entity.Builder builder = Entity.newBuilder(entity);
        
        // Set the upvotes property
        builder.set("upvotes", upvotes);
        
        // If downvotes doesn't exist, initialize it
        if (!entity.contains("downvotes")) {
            builder.set("downvotes", 0L);
        }
        
        Entity updatedEntity = builder.build();
        datastore.update(updatedEntity);
        
        // Return the updated story
        Story story = new Story.Builder()
                .withId(storyId)
                .withPrompt(entity.getString("prompt"))
                .withStory(entity.getString("story"))
                .withUpvotes(upvotes)
                .withDownvotes(entity.contains("downvotes") ? entity.getLong("downvotes") : 0)
                .build();
        
        return story;
    }

    public Story downvoteStory(String storyId) {
        Key key = storyKeyFactory.newKey(storyId);
        Entity entity = datastore.get(key);
        
        if (entity == null) {
            throw new RuntimeException("Story not found");
        }
        
        long downvotes = entity.contains("downvotes") ? entity.getLong("downvotes") + 1 : 1;
        
        // Create a new entity builder from the existing entity
        Entity.Builder builder = Entity.newBuilder(entity);
        
        // Set the downvotes property
        builder.set("downvotes", downvotes);
        
        // If upvotes doesn't exist, initialize it
        if (!entity.contains("upvotes")) {
            builder.set("upvotes", 0L);
        }
    
        Entity updatedEntity = builder.build();
        datastore.update(updatedEntity);
        
        // Return the updated story
        Story story = new Story.Builder()
                .withId(storyId)
                .withPrompt(entity.getString("prompt"))
                .withStory(entity.getString("story"))
                .withUpvotes(entity.contains("upvotes") ? entity.getLong("upvotes") : 0)
                .withDownvotes(downvotes)
                .build();
        
        return story;
    }

    public String getDatastoreInfo() {
        try {
            String projectId = datastore.getOptions().getProjectId();
            String namespace = datastore.getOptions().getNamespace();
            
            return "Connected to Datastore in project: " + projectId + 
                   (namespace != null && !namespace.isEmpty() ? ", namespace: " + namespace : ", default namespace");
        } catch (Exception e) {
            return "Error getting Datastore info: " + e.getMessage();
        }
    }
    
    /**
     * Get all entity kinds in the Datastore
     */
    public List<String> getAllKinds() {
        try {
            // Query for all keys
            Query<Key> query = Query.newKeyQueryBuilder().build();
            QueryResults<Key> results = datastore.run(query);
            
            Set<String> kinds = new HashSet<>();
            while (results.hasNext()) {
                Key key = results.next();
                kinds.add(key.getKind());
            }
            
            return new ArrayList<>(kinds);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Check for existence of entities with a specific kind
     */
    public boolean checkKindExists(String kind) {
        try {
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind(kind)
                    .setLimit(1)  // We only need to check if any exist
                    .build();
            
            QueryResults<Entity> results = datastore.run(query);
            return results.hasNext();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getEntityStructure() {
        try {
            // Query for one entity of the "story" kind
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind("story")
                    .setLimit(1)
                    .build();
            
            QueryResults<Entity> results = datastore.run(query);
            
            if (!results.hasNext()) {
                return "No story entities found to examine structure.";
            }
            
            Entity entity = results.next();
            StringBuilder response = new StringBuilder();
            response.append("Entity key: ").append(entity.getKey().getName() != null ? 
                             entity.getKey().getName() : entity.getKey().getId()).append("\n");
            response.append("Properties:\n");
            
            for (String propertyName : entity.getNames()) {
                Value<?> value = entity.getValue(propertyName);
                String type = value.getClass().getSimpleName();
                String stringValue = value.toString();
                if (stringValue.length() > 50) {
                    stringValue = stringValue.substring(0, 47) + "...";
                }
                response.append("- ").append(propertyName).append(" (").append(type).append("): ")
                       .append(stringValue).append("\n");
            }
            
            return response.toString();
        } catch (Exception e) {
            return "Failed to examine entity structure: " + e.getMessage();
        }
    }

    // Add at the top of the class:
private static final Logger logger = LoggerFactory.getLogger(PromptService.class);
    public Story saveStoryWithLogging(Story story) {
        try {
            logger.info("Attempting to save story with ID: {}", 
                       story.getId() != null ? story.getId() : "null");
            Story savedStory = saveStory(story);
            logger.info("Successfully saved story with ID: {}", savedStory.getId());
            return savedStory;
        } catch (Exception e) {
            logger.error("Error saving story: {}", e.getMessage(), e);
            throw e;
        }
    }
}