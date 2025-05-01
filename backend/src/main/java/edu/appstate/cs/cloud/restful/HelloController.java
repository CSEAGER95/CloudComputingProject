package edu.appstate.cs.cloud.restful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static String logClass = "HelloController";
    private Logger logger = LoggerFactory.getLogger(logClass);

    @GetMapping("/hello")
    private String hello(String name) {
        logger.info(String.format("Issued greeting to %s", name));
        return String.format("Hello, %s", name);
    }
}
