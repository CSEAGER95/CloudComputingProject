// Update PromptService.java to use Google Datastore
package edu.appstate.cs.cloud.restful.datastore;

import com.google.cloud.datastore.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.appstate.cs.cloud.restful.models.Prompt;
import edu.appstate.cs.cloud.restful.models.Story;

@Service
public class PromptService {
    private final Datastore datastore;
    private final KeyFactory storyKeyFactory;

    public PromptService() {
        // Initialize the Datastore client
        this.datastore = DatastoreOptions.getDefaultInstance().getService();
        this.storyKeyFactory = datastore.newKeyFactory().setKind("Story");
    }

    public List<Story> getAllStories() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Story")
                .build();
        
        QueryResults<Entity> results = datastore.run(query);
        List<Story> stories = new ArrayList<>();
        
        while (results.hasNext()) {
            Entity entity = results.next();
            
            Story story = new Story.Builder()
                    .withId(entity.getKey().getName())
                    .withPrompt(entity.getString("prompt"))
                    .withStory(entity.getString("story"))
                    .withUpvotes(entity.getLong("upvotes"))
                    .withDownvotes(entity.getLong("downvotes"))
                    .build();
            
            stories.add(story);
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
        
        long upvotes = entity.getLong("upvotes") + 1;
        
        Entity updatedEntity = Entity.newBuilder(entity)
                .set("upvotes", upvotes)
                .build();
        
        datastore.update(updatedEntity);
        
        // Return the updated story
        Story story = new Story.Builder()
                .withId(storyId)
                .withPrompt(entity.getString("prompt"))
                .withStory(entity.getString("story"))
                .withUpvotes(upvotes)
                .withDownvotes(entity.getLong("downvotes"))
                .build();
        
        return story;
    }

    public Story downvoteStory(String storyId) {
        Key key = storyKeyFactory.newKey(storyId);
        Entity entity = datastore.get(key);
        
        if (entity == null) {
            throw new RuntimeException("Story not found");
        }
        
        long downvotes = entity.getLong("downvotes") + 1;
        
        Entity updatedEntity = Entity.newBuilder(entity)
                .set("downvotes", downvotes)
                .build();
        
        datastore.update(updatedEntity);
        
        // Return the updated story
        Story story = new Story.Builder()
                .withId(storyId)
                .withPrompt(entity.getString("prompt"))
                .withStory(entity.getString("story"))
                .withUpvotes(entity.getLong("upvotes"))
                .withDownvotes(downvotes)
                .build();
        
        return story;
    }
}