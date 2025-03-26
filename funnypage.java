package com.storyapp.satire;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.schema.predict.instance.TextPredictionInstance;
import com.google.cloud.aiplatform.v1.schema.predict.prediction.TextPredictionResult;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;

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
import org.json.JSONObject;
import org.json.JSONException;

@SpringBootApplication
@RestController
public class funnypage {

    public static void main(String[] args) {
        SpringApplication.run(funnypage.class, args);
    }

    // Story object to match the database table
    private static class Story {
        private int id;
        private String title;
        private String content;
        private String prompt;
        private Integer userId;
        private Date createdAt;
        private boolean isFeatured;
        private Integer score; // For featured stories query

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        
        public boolean getIsFeatured() { return isFeatured; }
        public void setIsFeatured(boolean isFeatured) { this.isFeatured = isFeatured; }
        
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }
    
    // RowMapper for converting database rows to Story objects
    private static final RowMapper<Story> STORY_ROW_MAPPER = (ResultSet rs, int rowNum) -> {
        Story story = new Story();
        story.setId(rs.getInt("id"));
        story.setTitle(rs.getString("title"));
        story.setContent(rs.getString("content"));
        story.setPrompt(rs.getString("prompt"));
        story.setUserId(rs.getObject("user_id", Integer.class));
        story.setCreatedAt(rs.getTimestamp("created_at"));
        story.setIsFeatured(rs.getBoolean("is_featured"));
        
        // Check if the score column exists (for featured stories query)
        try {
            story.setScore(rs.getObject("score", Integer.class));
        } catch (SQLException e) {
            // Score column doesn't exist, that's okay
        }
        
        return story;
    };

    // Get secrets from Secret Manager
    private String getSecret(String secretId) throws IOException {
        String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
        
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        }
    }

    // Create database connection
    private DataSource getDataSource() throws IOException {
        String dbCredsJson = getSecret("db-credentials");
        JSONObject dbCreds = new JSONObject(dbCredsJson);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://cloudsql/" + dbCreds.getString("host") + 
                          "/" + dbCreds.getString("database"));
        dataSource.setUsername(dbCreds.getString("username"));
        dataSource.setPassword(dbCreds.getString("password"));
        
        return dataSource;
    }

    // Request class for story generation
    private static class GenerateStoryRequest {
        private String prompt;
        private String newsSource;
        private Integer userId;
        
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        
        public String getNewsSource() { return newsSource; }
        public void setNewsSource(String newsSource) { this.newsSource = newsSource; }
        
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
    }

    // Generate a satire story
    @PostMapping("/api/generate")
    public Map<String, Object> generateStory(@RequestBody GenerateStoryRequest request) throws Exception {
        // Initialize Gemini API
        String apiKey = getSecret("gemini-api-key");
        // In a real implementation, you would use this API key to configure the client
        
        // Prepare the prompt for Gemini
        String newsSource = request.getNewsSource() != null ? request.getNewsSource() : "";
        String geminiPrompt = String.format("""
            Create a satirical news article in the style of The Onion about: %s
            
            If provided, use this real news as inspiration: %s
            
            Make it absurd yet believable with:
            1. A catchy headline
            2. A short, punchy first paragraph
            3. Several paragraphs of escalating ridiculousness
            4. At least one fake quote from a fictional expert
            5. A conclusion that doubles down on the absurdity
            
            Format as a JSON object with 'title' and 'content' fields.
            """, request.getPrompt(), newsSource);
            
        // In a real implementation, you would use the Vertex AI Java SDK
        // Here's a placeholder for the actual implementation
        String responseText = callGeminiApi(geminiPrompt);
        
        // Parse the response
        Map<String, String> story;
        try {
            JSONObject jsonResponse = new JSONObject(responseText);
            story = new HashMap<>();
            story.put("title", jsonResponse.getString("title"));
            story.put("content", jsonResponse.getString("content"));
        } catch (JSONException e) {
            // Fallback parsing if not proper JSON
            String[] lines = responseText.split("\n");
            String title = lines[0].trim();
            String content = String.join("\n", Arrays.copyOfRange(lines, 1, lines.length)).trim();
            
            story = new HashMap<>();
            story.put("title", title);
            story.put("content", content);
        }
        
        // Save to database
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        int storyId = jdbcTemplate.queryForObject(
            "INSERT INTO stories (title, content, user_id, prompt) VALUES (?, ?, ?, ?) RETURNING id",
            Integer.class,
            story.get("title"), 
            story.get("content"), 
            request.getUserId(), 
            request.getPrompt()
        );
        
        // Return the story
        Map<String, Object> response = new HashMap<>();
        response.put("id", storyId);
        response.put("title", story.get("title"));
        response.put("content", story.get("content"));
        
        return response;
    }
    
    // This is a placeholder for the actual Gemini API call
    private String callGeminiApi(String prompt) {
        // In a real implementation, this would use the Vertex AI Java SDK
        // For now, returning a fake response
        return """
            {
                "title": "Area Man Shocked to Discover His Social Media Data Worth Exactly Three Bags of Chips",
                "content": "PORTLAND, OR — Local resident Mike Hernandez expressed profound surprise Wednesday upon learning that his entire digital footprint, meticulously cultivated across multiple platforms over a decade, has a market value precisely equivalent to three bags of potato chips.\\n\\n'I always assumed my personal information was being harvested for something important,' said Hernandez, staring blankly at the 'personalized offer' email promising him three free bags of chips in exchange for completing his social media profile. 'Ten years of vacation photos, political opinions, and relationship status updates... and this is what I get?'\\n\\nThe revelation came after Hernandez installed a new data transparency tool that calculated the exact monetary value of his digital presence to advertisers and data brokers.\\n\\nDr. Eleanor Wong, digital economy expert at the fictional Institute for Data Valuation, explained that Hernandez's case is not unusual. 'Most consumers vastly overestimate their data's worth,' Wong stated while munching on complimentary chips provided by the study's sponsor. 'The average American's complete online identity can be purchased for roughly the price of a movie ticket, or in this case, three bags of artificially flavored corn products.'\\n\\nAt press time, Hernandez was reportedly carefully selecting which chip flavors to redeem while simultaneously updating his privacy settings."
            }
            """;
    }

    // Get a story by ID
    @GetMapping("/api/stories/{storyId}")
    public Map<String, Object> getStory(@PathVariable int storyId) throws IOException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        
        try {
            Story story = jdbcTemplate.queryForObject(
                "SELECT * FROM stories WHERE id = ?",
                STORY_ROW_MAPPER,
                storyId
            );
            
            // Convert to map for JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("id", story.getId());
            response.put("title", story.getTitle());
            response.put("content", story.getContent());
            response.put("prompt", story.getPrompt());
            response.put("user_id", story.getUserId());
            response.put("created_at", story.getCreatedAt());
            response.put("is_featured", story.getIsFeatured());
            
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Story not found");
            return error;
        }
    }

    // Get featured stories
    @GetMapping("/api/stories/featured")
    public List<Map<String, Object>> getFeaturedStories() throws IOException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        
        List<Story> stories = jdbcTemplate.query(
            """
            SELECT s.*, 
                   (COALESCE(upvotes, 0) - COALESCE(downvotes, 0)) as score 
            FROM stories s
            LEFT JOIN (
                SELECT story_id, 
                       SUM(CASE WHEN vote_type = 'up' THEN 1 ELSE 0 END) as upvotes,
                       SUM(CASE WHEN vote_type = 'down' THEN 1 ELSE 0 END) as downvotes
                FROM votes
                GROUP BY story_id
            ) v ON s.id = v.story_id
            ORDER BY score DESC, created_at DESC
            LIMIT 10
            """,
            STORY_ROW_MAPPER
        );
        
        // Convert to list of maps for JSON response
        List<Map<String, Object>> response = new ArrayList<>();
        for (Story story : stories) {
            Map<String, Object> storyMap = new HashMap<>();
            storyMap.put("id", story.getId());
            storyMap.put("title", story.getTitle());
            storyMap.put("content", story.getContent());
            storyMap.put("prompt", story.getPrompt());
            storyMap.put("user_id", story.getUserId());
            storyMap.put("created_at", story.getCreatedAt());
            storyMap.put("is_featured", story.getIsFeatured());
            storyMap.put("score", story.getScore());
            response.add(storyMap);
        }
        
        return response;
    }
}