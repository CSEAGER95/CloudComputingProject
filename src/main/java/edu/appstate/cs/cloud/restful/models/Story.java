package edu.appstate.cs.cloud.restful.models;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = Story.Story)
public class Story {
    public String prompt;
    public String story;

    public static final String Story  = "story";
    public static final String Prompt  = "prompt";

    public Story(Builder builder) {
        this.prompt = builder.prompt;
        this.story = builder.story;
    }

    public static class Builder {
        private String prompt;
        private String story;

        public Builder withPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder withStory(String story) {
            this.story = story;
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

    @Override
    public String toString() {
        return story;
    }
}
