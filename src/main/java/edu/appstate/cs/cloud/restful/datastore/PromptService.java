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

import edu.appstate.cs.cloud.restful.models.Prompt;

@Service
public class PromptService {
    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final String ENTITY_KIND = "prompt";
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);

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
}