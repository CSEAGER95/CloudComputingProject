package edu.appstate.cs.cloud.restful.datastore;

import com.google.cloud.datastore.*;
import edu.appstate.cs.cloud.restful.models.Textbook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class TextbookService {
    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final String ENTITY_KIND = "Textbook";
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);

    public Key createTextbook(Textbook textbook) {
        Key key = datastore.allocateId(keyFactory.newKey());
        Entity textbookEntity = Entity.newBuilder(key)
                .set(Textbook.TITLE, textbook.getTitle())
                .set(Textbook.AUTHORS, textbook.getAuthors())
                .set(Textbook.SUBJECT, textbook.getSubject())
                .set(Textbook.PUBLISHER, textbook.getPublisher())
                .set(Textbook.YEAR, textbook.getYear())
                .set(Textbook.IMAGE_LINK, textbook.getImageLink())
                .build();
        datastore.put(textbookEntity);
        return key;
    }

    public List<Textbook> getAllTextbooks() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(ENTITY_KIND)
                .build();
        Iterator<Entity> entities = datastore.run(query);
        return buildTextbooks(entities);
    }

    public List<Textbook> getAllTextbooksForSubject(String subject) {
        // TODO: What code needs to be added here to retrieve textbooks for a given subject?
        // HINT: Look at the code linked on AsULearn, we need to filter our results. You need
        // to do this filtering in the query, you CANNOT just grab everything and then filter
        // it here using Java code.

        // TODO: Remove this return statement once you have something valid to return
        return Collections.emptyList();
    }

    private List<Textbook> buildTextbooks(Iterator<Entity> entities) {
        List<Textbook> textbooks = new ArrayList<>();
        entities.forEachRemaining(entity -> textbooks.add(entityToTextbook(entity)));
        return textbooks;
    }

    private Textbook entityToTextbook(Entity entity) {
        return new Textbook.Builder()
                .withTitle(entity.getString(Textbook.TITLE))
                .withAuthors(entity.getString(Textbook.AUTHORS))
                .withSubject(entity.getString(Textbook.SUBJECT))
                .withPublisher(entity.getString(Textbook.PUBLISHER))
                .withYear(entity.getLong(Textbook.YEAR))
                .withImageLink(entity.getString(Textbook.IMAGE_LINK))
                .build();
    }

}
