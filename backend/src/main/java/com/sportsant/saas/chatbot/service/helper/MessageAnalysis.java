package com.sportsant.saas.chatbot.service.helper;

import java.util.List;

public class MessageAnalysis {
    private String content;
    private IntentResult intent;
    private SentimentResult sentiment;
    private List<Entity> entities;
    private KnowledgeMatchResult knowledgeMatch;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public IntentResult getIntent() { return intent; }
    public void setIntent(IntentResult intent) { this.intent = intent; }
    public SentimentResult getSentiment() { return sentiment; }
    public void setSentiment(SentimentResult sentiment) { this.sentiment = sentiment; }
    public List<Entity> getEntities() { return entities; }
    public void setEntities(List<Entity> entities) { this.entities = entities; }
    public KnowledgeMatchResult getKnowledgeMatch() { return knowledgeMatch; }
    public void setKnowledgeMatch(KnowledgeMatchResult knowledgeMatch) { this.knowledgeMatch = knowledgeMatch; }
}
