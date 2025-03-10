package edu.appstate.cs.cloud.restful.api;

import edu.appstate.cs.cloud.restful.datastore.CourseService;
import edu.appstate.cs.cloud.restful.models.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/courses")
public class CourseEndpoint {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping(value = "/init")
    public boolean initCourses() {
        // Create some sample courses
        List<Course> courses = new ArrayList<>();

        courses.add(new Course.Builder()
                .withCourseName("Operating Systems")
                .withSubject("Computer Science")
                .build()
        );
        courses.add(new Course.Builder()
                .withCourseName("Programming Languages")
                .withSubject("Computer Science")
                .build()
        );
        courses.add(new Course.Builder()
                .withCourseName("Statistics")
                .withSubject("Mathematics")
                .build()
        );
        courses.add(new Course.Builder()
                .withCourseName("English Composition")
                .withSubject("English")
                .build()
        );
        courses.add(new Course.Builder()
                .withCourseName("Poems and Sonnets")
                .withSubject("English")
                .build()
        );

        for (Course c : courses) {
            courseService.createCourse(c);
        }

        return true;
    }
}
