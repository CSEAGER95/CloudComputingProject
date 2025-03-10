package edu.appstate.cs.cloud.restful.models;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("textbooks")
public class Textbook {
    private String title;
    private String authors;
    private String publisher;
    private String subject;
    private long year;
    private String imageLink;

    public static final String TITLE = "title";
    public static final String AUTHORS = "authors";
    public static final String PUBLISHER = "publisher";
    public static final String YEAR = "year";
    public static final String SUBJECT = "subject";
    public static final String IMAGE_LINK = "imageLink";

    private Textbook(Builder builder) {
        this.title = builder.title;
        this.authors = builder.authors;
        this.publisher = builder.publisher;
        this.subject = builder.subject;
        this.year = builder.year;
        this.imageLink = builder.imageLink;
    }

    public static class Builder {
        private String title;
        private String authors;
        private String publisher;
        private String subject;
        private long year;
        private String imageLink;

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withAuthors(String authors) {
            this.authors = authors;
            return this;
        }

        public Builder withPublisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder withYear(long year) {
            this.year = year;
            return this;
        }

        public Builder withImageLink(String imageLink) {
            this.imageLink = imageLink;
            return this;
        }

        public Textbook build() {
            return new Textbook(this);
        }
    }

    public Textbook() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public String toString() {
        return "Textbook{" +
                "title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", publisher='" + publisher + '\'' +
                ", subject='" + subject + '\'' +
                ", year=" + year + 
                ", imageLink='" + imageLink +  + '\'' +
                '}';
    }
}
