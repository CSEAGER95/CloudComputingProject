package edu.appstate.cs.cloud.restful.datastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StringValue;

import edu.appstate.cs.cloud.restful.models.Prompt;
import edu.appstate.cs.cloud.restful.models.Story;

@Service
public class PromptService {
    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final String ENTITY_KIND = "prompt";
    private static final String STORY_KIND = "story";
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);
    private final KeyFactory storyKeyFactory = datastore.newKeyFactory().setKind(STORY_KIND);

    public Key createPrompt(Prompt prompt) {
        Key key = datastore.allocateId(keyFactory.newKey());
        Entity promptEntity = Entity.newBuilder(key)
                .set(Prompt.PROMPT, prompt.getPrompt())
                .build();
        datastore.put(promptEntity);
        return key;
    }

    public List<Prompt> getAllPrompts() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(ENTITY_KIND)
                .build();
        Iterator<Entity> entities = datastore.run(query);
        return buildPrompts(entities);
    }

    private List<Prompt> buildPrompts(Iterator<Entity> entities) {
        List<Prompt> prompts = new ArrayList<>();
        entities.forEachRemaining(entity -> prompts.add(entityToPrompt(entity)));
        return prompts;
    }

    private Prompt entityToPrompt(Entity entity) {
        return new Prompt.Builder()
                .withPrompt(entity.getString(Prompt.PROMPT))
                .build();
    }
    
    // Add methods for Story manipulation
    
    public Story upvoteStory(String storyId) {
        Key key = storyKeyFactory.newKey(storyId);
        Entity storyEntity = datastore.get(key);
        
        if (storyEntity == null) {
            throw new RuntimeException("Story not found with ID: " + storyId);
        }
        
        long upvotes = storyEntity.getLong(Story.Upvotes) + 1;
        
        Entity updatedEntity = Entity.newBuilder(storyEntity)
                .set(Story.Upvotes, upvotes)
                .build();
        
        datastore.update(updatedEntity);
        
        return entityToStory(updatedEntity);
    }
    
    public Story downvoteStory(String storyId) {
        Key key = storyKeyFactory.newKey(storyId);
        Entity storyEntity = datastore.get(key);
        
        if (storyEntity == null) {
            throw new RuntimeException("Story not found with ID: " + storyId);
        }
        
        long downvotes = storyEntity.getLong(Story.Downvotes) + 1;
        
        Entity updatedEntity = Entity.newBuilder(storyEntity)
                .set(Story.Downvotes, downvotes)
                .build();
        
        datastore.update(updatedEntity);
        
        return entityToStory(updatedEntity);
    }
    
    private Story entityToStory(Entity entity) {
        return new Story.Builder()
                .withId(entity.getKey().getName())
                .withPrompt(entity.getString(Story.Prompt))
                .withStory(entity.getString(Story.Story))
                .withUpvotes(entity.getLong(Story.Upvotes))
                .withDownvotes(entity.getLong(Story.Downvotes))
                .build();
    }
}