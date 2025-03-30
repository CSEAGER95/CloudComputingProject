import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;

@SpringBootApplication
@RestController
public class UserInputController {
    private static String logClass = "UserInputController";
    private Logger logger = LoggerFactory.getLogger(logClass);
    
    // List to store inputs (for simple in-memory storage)
    private List<UserInput> userInputs = new ArrayList<>();
    
    public static void main(String[] args) {
        SpringApplication.run(UserInputController.class, args);
    }
    
    // Model class for user input
    public static class UserInput {
        private int id;
        private String content;
        private Date createdAt;
        
        public UserInput() {
            this.createdAt = new Date();
        }
        
        public UserInput(String content) {
            this.content = content;
            this.createdAt = new Date();
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    }
    
    // Simple GET endpoint that receives inputs via query parameter
    @GetMapping("/input")
    public Map<String, Object> receiveInputGet(@RequestParam String message) {
        UserInput input = new UserInput(message);
        input.setId(userInputs.size() + 1);
        userInputs.add(input);
        
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
        input.setId(userInputs.size() + 1);
        userInputs.add(input);
        
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
    public List<Map<String, Object>> getAllInputs() {
        logger.info("Retrieving all inputs");
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (UserInput input : userInputs) {
            Map<String, Object> inputMap = new HashMap<>();
            inputMap.put("id", input.getId());
            inputMap.put("content", input.getContent());
            inputMap.put("timestamp", input.getCreatedAt());
            response.add(inputMap);
        }
        
        return response;
    }
    
    // Get input by ID
    @GetMapping("/inputs/{id}")
    public Map<String, Object> getInputById(@PathVariable int id) {
        logger.info(String.format("Retrieving input with ID: %d", id));
        
        for (UserInput input : userInputs) {
            if (input.getId() == id) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", input.getId());
                response.put("content", input.getContent());
                response.put("timestamp", input.getCreatedAt());
                return response;
            }
        }
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Input not found");
        return error;
    }
}