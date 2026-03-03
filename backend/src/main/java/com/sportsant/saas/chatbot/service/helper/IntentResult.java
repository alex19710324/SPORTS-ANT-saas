package com.sportsant.saas.chatbot.service.helper;

public class IntentResult {
    private String intent; // GREETING, QUESTION, COMPLAINT, BOOKING
    private Double confidence; // 0-1

    public IntentResult() {}

    public IntentResult(String intent, Double confidence) {
        this.intent = intent;
        this.confidence = confidence;
    }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
