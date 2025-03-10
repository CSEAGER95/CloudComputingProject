package edu.appstate.cs.cloud.restful.datastore;

import com.google.cloud.datastore.*;
import edu.appstate.cs.cloud.restful.models.Course;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CourseService {
    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final String ENTITY_KIND = "Course";
    private final KeyFactory keyFactory = datastore.newKeyFactory().setKind(ENTITY_KIND);

    public Key createCourse(Course course) {
        Key key = datastore.allocateId(keyFactory.newKey());
        Entity courseEntity = Entity.newBuilder(key)
                .set(Course.COURSE_NAME, course.getCourseName())
                .set(Course.SUBJECT, course.getSubject())
                .build();
        datastore.put(courseEntity);
        return key;
    }

    public List<Course> getAllCourses() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind(ENTITY_KIND)
                .build();
        Iterator<Entity> entities = datastore.run(query);
        return buildCourses(entities);
    }

    private List<Course> buildCourses(Iterator<Entity> entities) {
        List<Course> courses = new ArrayList<>();
        entities.forEachRemaining(entity -> courses.add(entityToCourse(entity)));
        return courses;
    }

    private Course entityToCourse(Entity entity) {
        return new Course.Builder()
                .withCourseName(entity.getString(Course.COURSE_NAME))
                .withSubject(entity.getString(Course.SUBJECT))
                .build();
    }
}
