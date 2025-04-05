package edu.appstate.cs.cloud.restful;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_inputs")
public class UserInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String content;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
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