package com.sportsant.saas.chatbot.service.helper;

public class SentimentResult {
    private String sentiment; // POSITIVE, NEUTRAL, NEGATIVE
    private Double score; // -1到1

    public SentimentResult() {}

    public SentimentResult(String sentiment, Double score) {
        this.sentiment = sentiment;
        this.score = score;
    }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
