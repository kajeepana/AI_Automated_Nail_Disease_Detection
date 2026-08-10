package com.example.aiautomatednaildiseasedetection.model;

public class NailAnalysis {

    private Long id;
    private String email;
    private String imageName;
    private String imagePath;
    private String segmentationMaskPath;
    private String predictedCondition;
    private Double confidence;
    private Double severityScore;
    private String severityLabel;
    private String status;
    private String modelVersion;

    public NailAnalysis() {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSegmentationMaskPath() {
        return segmentationMaskPath;
    }

    public void setSegmentationMaskPath(String segmentationMaskPath) {
        this.segmentationMaskPath = segmentationMaskPath;
    }

    public String getPredictedCondition() {
        return predictedCondition;
    }

    public void setPredictedCondition(String predictedCondition) {
        this.predictedCondition = predictedCondition;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Double getSeverityScore() {
        return severityScore;
    }

    public void setSeverityScore(Double severityScore) {
        this.severityScore = severityScore;
    }

    public String getSeverityLabel() {
        return severityLabel;
    }

    public void setSeverityLabel(String severityLabel) {
        this.severityLabel = severityLabel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}