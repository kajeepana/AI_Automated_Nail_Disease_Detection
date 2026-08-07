package com.example.aiautomatednaildiseasedetection.model;

public class Upload {

    private Long id;
    private String email;
    private String imageName;
    private String prediction;

    public Upload() {
    }

    public Upload(Long id, String email, String imageName, String prediction) {
        this.id = id;
        this.email = email;
        this.imageName = imageName;
        this.prediction = prediction;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }
}