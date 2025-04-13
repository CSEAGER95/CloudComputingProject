package edu.appstate.cs.cloud.restful.datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StringValue;

import edu.appstate.cs.cloud.restful.models.Story;
import edu.appstate.cs.cloud.restful.StoryGenerator;

@Service
public class PromptService {

    @Autowired
    private StoryGenerator StoryGenerator;

    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final String ENTITY_KIND = "story";
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);

    public Story createStory(String prompt) throws IOException {
        Key key = datastore.allocateId(keyFactory.newKey());

        var generatedStoryText = StoryGenerator.generate(prompt);

        Story story = new Story(new Story.Builder()
            .withPrompt(prompt)
            .withStory(generatedStoryText)); // Replace

        Entity storyEntity = Entity.newBuilder(key)
            .set(Story.Prompt, story.getPrompt())
            .set(Story.Story, StringValue.newBuilder(story.getStory()).setExcludeFromIndexes(true).build())
            .build();
        datastore.put(storyEntity);
        return story;
    }

    public List<Story> getAllStories() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(ENTITY_KIND)
                .build();
        Iterator<Entity> entities = datastore.run(query);
        return buildStories(entities);
    }

    private List<Story> buildStories(Iterator<Entity> entities) {
        List<Story> stories = new ArrayList<>();
        entities.forEachRemaining(entity -> stories.add(entityToStories(entity)));
        return stories;
    }

    private Story entityToStories(Entity entity) {
        return new Story.Builder()
                .withStory(entity.getString(Story.Story))
                .withPrompt(entity.getString(Story.Prompt))
                .build();
    }
}
