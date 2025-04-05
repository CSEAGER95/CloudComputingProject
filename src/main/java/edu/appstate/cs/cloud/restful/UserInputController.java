package edu.appstate.cs.cloud.restful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.*;

@SpringBootApplication
@RestController
public class UserInputController {
    private static String logClass = "UserInputController";
    private Logger logger = LoggerFactory.getLogger(logClass);
    
    @Autowired
    private UserInputRepository userInputRepository;
    
    public static void main(String[] args) {
        SpringApplication.run(UserInputController.class, args);
    }
    
    // Simple GET endpoint that receives inputs via query parameter
    @GetMapping("/input")
    public Map<String, Object> receiveInputGet(@RequestParam String message) {
        UserInput input = new UserInput(message);
        input = userInputRepository.save(input);
        
        logger.info(String.format("Received input via GET: %s", message));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", input.getId());
        response.put("message", "Input received");
        response.put("content", input.getContent());
        response.put("timestamp", input.getCreatedAt());
        
        return response;
    }
    
    // POST endpoint that receives inputs via JSON body
    @PostMapping("/input")
    public Map<String, Object> receiveInputPost(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        UserInput input = new UserInput(message);
        input = userInputRepository.save(input);
        
        logger.info(String.format("Received input via POST: %s", message));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", input.getId());
        response.put("message", "Input received");
        response.put("content", input.getContent());
        response.put("timestamp", input.getCreatedAt());
        
        return response;
    }
    
    // Get all stored inputs
    @GetMapping("/inputs")
    public List<UserInput> getAllInputs() {
        logger.info("Retrieving all inputs");
        return userInputRepository.findAll();
    }
    
    // Get input by ID
    @GetMapping("/inputs/{id}")
    public ResponseEntity<?> getInputById(@PathVariable int id) {
        logger.info(String.format("Retrieving input with ID: %d", id));
        
        return userInputRepository.findById(id)
            .map(input -> ResponseEntity.ok(input))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "Input not found")));
    }
}