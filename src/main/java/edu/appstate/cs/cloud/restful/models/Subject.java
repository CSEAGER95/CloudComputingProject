package edu.appstate.cs.cloud.restful.models;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("subjects")
public class Subject {
    private String subjectName;

    public static final String SUBJECT_NAME = "subjectName";

    private Subject(Builder builder) {
        this.subjectName = builder.subjectName;
    }

    public static class Builder {
        private String subjectName;

        public Builder withSubjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public Subject build() {
            return new Subject(this);
        }
    }

    public Subject() {

    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectName='" + subjectName + '\'' +
                '}';
    }
}
