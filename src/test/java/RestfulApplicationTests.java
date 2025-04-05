import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.gcp.datastore.emulator.enabled=true",
    "spring.cloud.gcp.datastore.project-id=test-project"
})
class RestfulApplicationTests {

    // Define the Prompt class
    static class Prompt {
        private Long id;
        private String content;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Autowired
    private DatastoreTemplate datastoreTemplate;

    @BeforeEach
    void setUp() {
        // Clear test database before each test
        datastoreTemplate.deleteAll(Prompt.class);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testDatabaseConnection() {
        assertTrue(datastoreTemplate != null);
    }

    @Test
    void testPromptStorage() {
        // Add test implementation for prompt storage
        Prompt prompt = new Prompt();
        prompt.setContent("Test prompt");
        
        Prompt savedPrompt = datastoreTemplate.save(prompt);
        assertNotNull(savedPrompt.getId());
        
        Prompt retrievedPrompt = datastoreTemplate.findById(savedPrompt.getId(), Prompt.class);
        assertNotNull(retrievedPrompt);
        assertEquals("Test prompt", retrievedPrompt.getContent());
    }
}
