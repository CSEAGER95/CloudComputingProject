package edu.appstate.cs.cloud.restful.models;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "prompt")
public class Prompt {
    public String prompt;

    public static final String PROMPT  = "prompt";

    public Prompt(Builder builder) {
        this.prompt = builder.prompt;
    }

    public static class Builder {
        private String prompt;

        public Builder withCourseName(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder withPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Prompt build() {
            return new Prompt(this);
        }
    }

    public Prompt input() {
        return this;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return "Course: " + prompt;
    }
}
