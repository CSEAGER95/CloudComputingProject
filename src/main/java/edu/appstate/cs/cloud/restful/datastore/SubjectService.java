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

import edu.appstate.cs.cloud.restful.models.Subject;

@Service
public class SubjectService {
    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final String ENTITY_KIND = "Subject";
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);

    public Key createSubject(Subject subject) {
        Key key = datastore.allocateId(keyFactory.newKey());
        Entity subjectEntity = Entity.newBuilder(key)
                .set(Subject.SUBJECT_NAME, subject.getSubjectName())
                .build();
        datastore.put(subjectEntity);
        return key;
    }

    public List<Subject> getAllSubjects() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(ENTITY_KIND)
                .build();
        Iterator<Entity> entities = datastore.run(query);
        return buildSubjects(entities);
    }

    private List<Subject> buildSubjects(Iterator<Entity> entities) {
        List<Subject> subjects = new ArrayList<>();
        entities.forEachRemaining(entity -> subjects.add(entityToSubject(entity)));
        return subjects;
    }

    private Subject entityToSubject(Entity entity) {
        return new Subject.Builder()
                .withSubjectName(entity.getString(Subject.SUBJECT_NAME))
                .build();
    }
}