package edu.appstate.cs.cloud.restful.models;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = Story.Story)
public class Story {
    public String prompt;
    public String story;
    public long upvotes;
    public long downvotes;
    public String id;  // Added to store the entity ID

    public static final String Story = "story";
    public static final String Prompt = "prompt";
    public static final String Upvotes = "upvotes";
    public static final String Downvotes = "downvotes";

    public Story(Builder builder) {
        this.prompt = builder.prompt;
        this.story = builder.story;
        this.upvotes = builder.upvotes;
        this.downvotes = builder.downvotes;
        this.id = builder.id;
    }

    public static class Builder {
        private String prompt;
        private String story;
        private long upvotes = 0;  // Default to 0
        private long downvotes = 0;  // Default to 0
        private String id;

        public Builder withPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder withStory(String story) {
            this.story = story;
            return this;
        }

        public Builder withUpvotes(long upvotes) {
            this.upvotes = upvotes;
            return this;
        }

        public Builder withDownvotes(long downvotes) {
            this.downvotes = downvotes;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Story build() {
            return new Story(this);
        }
    }

    public Story input() {
        return this;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public long getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(long upvotes) {
        this.upvotes = upvotes;
    }

    public long getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(long downvotes) {
        this.downvotes = downvotes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return story;
    }
}