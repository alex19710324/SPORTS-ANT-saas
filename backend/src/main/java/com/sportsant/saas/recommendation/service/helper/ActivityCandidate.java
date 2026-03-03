package com.sportsant.saas.recommendation.service.helper;

import java.util.Set;

public class ActivityCandidate {
    private Long id;
    private String name;
    private String type;
    private Set<String> tags;
    private Double popularityScore; // 0-100

    public ActivityCandidate() {}

    public ActivityCandidate(Long id, String name, String type, Set<String> tags, Double popularityScore) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.tags = tags;
        this.popularityScore = popularityScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public Double getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Double popularityScore) { this.popularityScore = popularityScore; }
}
