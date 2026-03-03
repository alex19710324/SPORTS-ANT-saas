package com.sportsant.saas.chatbot.service.helper;

public class Entity {
    private String name;
    private String value;
    private String type;
    private Double confidence;

    public Entity() {}

    public Entity(String name, String value, String type, Double confidence) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.confidence = confidence;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
