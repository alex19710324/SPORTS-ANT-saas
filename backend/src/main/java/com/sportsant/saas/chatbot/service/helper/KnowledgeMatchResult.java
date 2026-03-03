package com.sportsant.saas.chatbot.service.helper;

public class KnowledgeMatchResult {
    private String knowledgeId;
    private String title;
    private String content;
    private Double score;

    public KnowledgeMatchResult() {}

    public KnowledgeMatchResult(String knowledgeId, String title, String content, Double score) {
        this.knowledgeId = knowledgeId;
        this.title = title;
        this.content = content;
        this.score = score;
    }

    public String getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(String knowledgeId) { this.knowledgeId = knowledgeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
