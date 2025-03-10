package edu.appstate.cs.cloud.restful.api;

import edu.appstate.cs.cloud.restful.datastore.TextbookService;
import edu.appstate.cs.cloud.restful.models.Textbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/textbooks")
public class TextbookEndpoint {
    @Autowired
    private TextbookService textbookService;

    @GetMapping
    public List<Textbook> getAllTextbooks(@RequestParam("subject") Optional<String> subject) {
        // TODO: No change needed, see comment below.
        // If you aren't familiar with Java's Optional type, you can see here how it is
        // used -- in this case, so we know if we received a parameter as part of this call.
        if (subject.isPresent()) {
            return textbookService.getAllTextbooksForSubject(subject.get());
        }
        return textbookService.getAllTextbooks();
    }

    @GetMapping(value = "/init")
    public boolean initTextbooks() {
        // Create some sample textbooks
        List<Textbook> textbooks = new ArrayList<>();

        textbooks.add(new Textbook.Builder()
                .withTitle("Operating System Concepts")
                .withAuthors("Abraham Silberschatz, Greg Gagne, Peter B. Galvin")
                .withSubject("Computer Science")
                .withPublisher("Wiley")
                .withYear(2018)
                // TODO: Replace this with your own image, which can just be a copy of this one!
                .withImageLink("https://storage.googleapis.com/cloud-spring-2025-hills-hw3/book-cover-001.jpeg")
                .build());

        textbooks.add(new Textbook.Builder()
                .withTitle("Programming Language Pragmatics")
                .withAuthors("Michael Scott")
                .withSubject("Computer Science")
                .withPublisher("Elsevier")
                .withYear(2015)
                // TODO: Replace this with your own image, which can just be a copy of this one!
                .withImageLink("https://storage.googleapis.com/cloud-spring-2025-hills-hw3/book-cover-002.jpeg")
                .build());

        textbooks.add(new Textbook.Builder()
                .withTitle("Concepts of Programming Languages")
                .withAuthors("Robert W. Sebesta")
                .withSubject("Computer Science")
                .withPublisher("Pearson")
                .withYear(2016)
                // TODO: Replace this with your own image, which can just be a copy of this one!
                .withImageLink("https://storage.googleapis.com/cloud-spring-2025-hills-hw3/book-cover-003.jpeg")
                .build());

        textbooks.add(new Textbook.Builder()
                .withTitle("An Introduction to Statistical Learning: with Applications in R")
                .withAuthors("Gareth James, Daniela Witten, Trevor Hastie, and Robert Tibshirani")
                .withSubject("Mathematics")
                .withPublisher("Springer")
                .withYear(2017)
                // TODO: Replace this with your own image, which can just be a copy of this one!
                .withImageLink("https://storage.googleapis.com/cloud-spring-2025-hills-hw3/book-cover-004.jpeg")
                .build());

        for (Textbook t : textbooks) {
            textbookService.createTextbook(t);
        }

        return true;
    }
}
