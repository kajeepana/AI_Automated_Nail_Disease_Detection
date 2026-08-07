package com.example.aiautomatednaildiseasedetection.model;

public class Feedback {

    private Long id;
    private String email;
    private String feedback;

    public Feedback() {
    }

    public Feedback(Long id, String email, String feedback) {
        this.id = id;
        this.email = email;
        this.feedback = feedback;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}