package edu.appstate.cs.cloud.restful.datastore;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

import edu.appstate.cs.cloud.restful.models.Prompt;
import edu.appstate.cs.cloud.restful.models.Story;

@Service
public class PromptService {
    private List<Story> stories = new ArrayList<>();
    private List<Prompt> prompts = new ArrayList<>();

    public List<Story> getAllStories() {
        return stories;
    }

    public List<Prompt> getAllPrompts() {
        return prompts;
    }

    public Story saveStory(Story story) {
        stories.add(story);
        return story;
    }

    public Story upvoteStory(String storyId) {
        // Find and update story with matching ID
        for (Story story : stories) {
            if (story.getId().equals(storyId)) {
                story.setUpvotes(story.getUpvotes() + 1);
                return story;
            }
        }
        throw new RuntimeException("Story not found");
    }

    public Story downvoteStory(String storyId) {
        // Find and update story with matching ID
        for (Story story : stories) {
            if (story.getId().equals(storyId)) {
                story.setDownvotes(story.getDownvotes() + 1);
                return story;
            }
        }
        throw new RuntimeException("Story not found");
    }
}