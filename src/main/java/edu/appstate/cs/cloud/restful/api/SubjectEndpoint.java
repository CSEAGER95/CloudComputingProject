package edu.appstate.cs.cloud.restful.api;

import edu.appstate.cs.cloud.restful.datastore.SubjectService;
import edu.appstate.cs.cloud.restful.models.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/subjects")
public class SubjectEndpoint {
    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public List<Subject> getAllSubjects() {
        // TODO: What code is needed here to return all subjects?

        // TODO: You can remove this return statement once you are returning something valid.
        return Collections.emptyList();
    }

    @GetMapping(value = "/init")
    public boolean initSubjects() {
        // Create some sample subjects
        List<Subject> subjects = new ArrayList<>();

        subjects.add(new Subject.Builder().withSubjectName("Computer Science").build());
        subjects.add(new Subject.Builder().withSubjectName("Mathematics").build());
        subjects.add(new Subject.Builder().withSubjectName("English").build());
        subjects.add(new Subject.Builder().withSubjectName("History").build());

        // TODO: Is something missing here? If so, add the missing code...

        return true;
    }
}
