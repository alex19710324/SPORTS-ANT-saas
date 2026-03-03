package com.sportsant.saas.chatbot.service;

import com.sportsant.saas.chatbot.entity.ChatConversation;
import com.sportsant.saas.chatbot.entity.ChatMessage;
import com.sportsant.saas.chatbot.entity.KnowledgeBaseItem;
import com.sportsant.saas.chatbot.repository.ChatConversationRepository;
import com.sportsant.saas.chatbot.repository.ChatMessageRepository;
import com.sportsant.saas.chatbot.repository.KnowledgeBaseRepository;
import com.sportsant.saas.chatbot.service.helper.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class IntelligentChatbotService {
    
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IntelligentChatbotService(ChatConversationRepository conversationRepository,
                                     ChatMessageRepository messageRepository,
                                     KnowledgeBaseRepository knowledgeBaseRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }
    
    // 意图识别关键词
    private static final Map<String, List<String>> INTENT_KEYWORDS = Map.of(
        "GREETING", List.of("你好", "嗨", "hello", "hi", "早上好", "晚上好"),
        "MEMBERSHIP", List.of("会员", "注册", "登录", "密码", "账户", "会员卡"),
        "BOOKING", List.of("预约", "预订", "课程", "活动", "时间", "取消"),
        "PAYMENT", List.of("支付", "付款", "费用", "价格", "退款", "发票"),
        "FACILITY", List.of("设备", "器材", "跑步机", "游泳池", "更衣室", "淋浴"),
        "COMPLAINT", List.of("投诉", "问题", "不好", "差", "不满意", "投诉"),
        "THANKS", List.of("谢谢", "感谢", "辛苦了", "很好", "满意")
    );
    
    // 实体识别模式
    private static final Map<String, Pattern> ENTITY_PATTERNS = Map.of(
        "TIME", Pattern.compile("(\\d{1,2}[:：]\\d{1,2}|\\d+点|\\d+号|今天|明天|后天)"),
        "PRICE", Pattern.compile("(\\d+(\\.\\d+)?元|¥\\d+|\\d+块钱)"),
        "PHONE", Pattern.compile("(1[3-9]\\d{9})"),
        "EMAIL", Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})")
    );
    
    /**
     * 处理用户消息
     */
    @Transactional
    public ChatResponse processUserMessage(ChatRequest request) {
        System.out.println("处理用户消息: sessionId=" + request.getSessionId() + ", content=" + request.getContent());
        
        // 获取或创建对话
        ChatConversation conversation = getOrCreateConversation(
            request.getSessionId(), request.getTenantId(), 
            request.getMemberId(), request.getMemberName(), request.getChannel()
        );
        
        // 保存用户消息
        ChatMessage userMessage = saveUserMessage(conversation, request);
        
        // 分析用户消息
        MessageAnalysis analysis = analyzeMessage(request.getContent());
        
        // 更新对话状态
        updateConversationWithAnalysis(conversation, analysis);
        
        // 生成回复
        ChatResponse response = generateResponse(conversation, userMessage, analysis);
        
        // 保存机器人回复
        if (response != null && response.getContent() != null) {
            saveBotMessage(conversation, response.getContent(), analysis);
        }
        
        // 检查是否需要转人工
        if (shouldEscalateToHuman(conversation, analysis)) {
            escalateToHuman(conversation, analysis);
            response.setEscalated(true);
            response.setEscalationReason("问题复杂，已转人工客服");
        }
        
        return response;
    }
    
    /**
     * 获取或创建对话
     */
    private ChatConversation getOrCreateConversation(String sessionId, String tenantId, 
                                                    Long memberId, String memberName, String channel) {
        return conversationRepository.findBySessionIdAndTenantId(sessionId, tenantId)
            .orElseGet(() -> {
                ChatConversation newConversation = new ChatConversation();
                newConversation.setSessionId(sessionId);
                newConversation.setTenantId(tenantId);
                newConversation.setMemberId(memberId);
                newConversation.setMemberName(memberName);
                newConversation.setChannel(channel);
                newConversation.setStatus("ACTIVE");
                newConversation.setPriority("MEDIUM");
                newConversation.setCategory("GENERAL");
                newConversation.setFirstMessageAt(LocalDateTime.now());
                
                return conversationRepository.save(newConversation);
            });
    }
    
    /**
     * 保存用户消息
     */
    private ChatMessage saveUserMessage(ChatConversation conversation, ChatRequest request) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setTenantId(conversation.getTenantId());
        message.setSenderType("USER");
        message.setSenderId(request.getMemberId() != null ? request.getMemberId().toString() : "anonymous");
        message.setSenderName(request.getMemberName() != null ? request.getMemberName() : "用户");
        message.setContent(request.getContent());
        message.setContentType("TEXT");
        
        return messageRepository.save(message);
    }
    
    /**
     * 分析用户消息
     */
    private MessageAnalysis analyzeMessage(String content) {
        MessageAnalysis analysis = new MessageAnalysis();
        analysis.setContent(content);
        
        // 1. 意图识别
        IntentResult intentResult = detectIntent(content);
        analysis.setIntent(intentResult);
        
        // 2. 实体提取
        List<Entity> entities = extractEntities(content);
        analysis.setEntities(entities);
        
        // 3. 情感分析
        SentimentResult sentimentResult = analyzeSentiment(content);
        analysis.setSentiment(sentimentResult);
        
        // 4. 知识库匹配
        KnowledgeMatchResult knowledgeMatch = matchKnowledgeBase(content, intentResult.getIntent());
        analysis.setKnowledgeMatch(knowledgeMatch);
        
        return analysis;
    }

    /**
     * 意图识别
     */
    private IntentResult detectIntent(String content) {
        content = content.toLowerCase();
        
        Map<String, Integer> intentScores = new HashMap<>();
        
        // 基于关键词的意图识别
        for (Map.Entry<String, List<String>> entry : INTENT_KEYWORDS.entrySet()) {
            String intent = entry.getKey();
            List<String> keywords = entry.getValue();
            
            int score = 0;
            for (String keyword : keywords) {
                if (content.contains(keyword.toLowerCase())) {
                    score++;
                }
            }
            
            if (score > 0) {
                intentScores.put(intent, score);
            }
        }
        
        // 如果没有匹配到意图，使用默认意图
        if (intentScores.isEmpty()) {
            return new IntentResult("QUESTION", 0.3);
        }
        
        // 选择分数最高的意图
        String primaryIntent = intentScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("QUESTION");
        
        // 计算置信度
        double confidence = Math.min(0.9, intentScores.get(primaryIntent) * 0.2);
        
        return new IntentResult(primaryIntent, confidence);
    }
    
    /**
     * 实体提取
     */
    private List<Entity> extractEntities(String content) {
        List<Entity> entities = new ArrayList<>();
        
        for (Map.Entry<String, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
            String entityType = entry.getKey();
            Pattern pattern = entry.getValue();
            
            java.util.regex.Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                Entity entity = new Entity();
                entity.setType(entityType);
                entity.setValue(matcher.group());
                // entity.setStartIndex(matcher.start()); // Removed from Entity POJO to simplify
                // entity.setEndIndex(matcher.end());
                entity.setConfidence(1.0);
                entities.add(entity);
            }
        }
        
        return entities;
    }
    
    /**
     * 情感分析
     */
    private SentimentResult analyzeSentiment(String content) {
        // 简化情感分析
        String lowerContent = content.toLowerCase();
        
        // 正面词汇
        Set<String> positiveWords = Set.of("好", "很好", "非常好", "谢谢", "感谢", "满意", "喜欢");
        // 负面词汇
        Set<String> negativeWords = Set.of("不好", "差", "糟糕", "问题", "投诉", "不满意", "讨厌");
        
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (String word : positiveWords) {
            if (lowerContent.contains(word)) positiveCount++;
        }
        
        for (String word : negativeWords) {
            if (lowerContent.contains(word)) negativeCount++;
        }
        
        double score = 0.0;
        String sentiment = "NEUTRAL";
        
        if (positiveCount > negativeCount) {
            score = 0.5 + (positiveCount * 0.1);
            sentiment = "POSITIVE";
        } else if (negativeCount > positiveCount) {
            score = -0.5 - (negativeCount * 0.1);
            sentiment = "NEGATIVE";
        }
        
        // 限制在-1到1之间
        score = Math.max(-1.0, Math.min(1.0, score));
        
        return new SentimentResult(sentiment, score);
    }

    /**
     * 更新对话状态
     */
    private void updateConversationWithAnalysis(ChatConversation conversation, MessageAnalysis analysis) {
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setMessageCount(conversation.getMessageCount() + 1);
        
        // 更新意图
        if (analysis.getIntent() != null) {
            conversation.setPrimaryIntent(analysis.getIntent().getIntent());
            conversation.setIntentConfidence(analysis.getIntent().getConfidence());
        }
        
        // 更新情感
        if (analysis.getSentiment() != null) {
            conversation.setSentiment(analysis.getSentiment().getSentiment());
            conversation.setSentimentScore(analysis.getSentiment().getScore());
        }
        
        // 更新分类
        String category = mapIntentToCategory(analysis.getIntent().getIntent());
        conversation.setCategory(category);
        
        // 更新优先级
        if ("COMPLAINT".equals(analysis.getIntent().getIntent()) || 
            "NEGATIVE".equals(analysis.getSentiment().getSentiment())) {
            conversation.setPriority("HIGH");
        }
        
        // 保存实体信息
        try {
            conversation.setIntentEntitiesJson(objectMapper.writeValueAsString(analysis.getEntities()));
        } catch (JsonProcessingException e) {
            conversation.setIntentEntitiesJson("[]");
        }
        
        conversationRepository.save(conversation);
    }
    
    /**
     * 生成回复
     */
    private ChatResponse generateResponse(ChatConversation conversation, ChatMessage userMessage, 
                                         MessageAnalysis analysis) {
        ChatResponse response = new ChatResponse();
        response.setConversationId(conversation.getId());
        response.setSessionId(conversation.getSessionId());
        response.setTimestamp(LocalDateTime.now());
        
        // 检查知识库匹配
        if (analysis.getKnowledgeMatch() != null && analysis.getKnowledgeMatch().getKnowledgeId() != null) {
            // KnowledgeBaseItem knowledgeItem = analysis.getKnowledgeMatch().getKnowledgeItem(); // Need to fetch or store content
            // Assuming KnowledgeMatchResult stores content directly now
            
            response.setContent(analysis.getKnowledgeMatch().getContent());
            response.setSource("KNOWLEDGE_BASE");
            response.setKnowledgeId(analysis.getKnowledgeMatch().getKnowledgeId());
            response.setConfidence(analysis.getKnowledgeMatch().getScore());
            
            // 标记消息已匹配知识库
            userMessage.setKnowledgeMatched(true);
            userMessage.setKnowledgeId(analysis.getKnowledgeMatch().getKnowledgeId());
            userMessage.setKnowledgeTitle(analysis.getKnowledgeMatch().getTitle());
            userMessage.setKnowledgeMatchScore(analysis.getKnowledgeMatch().getScore());
            messageRepository.save(userMessage);
            
        } else {
            // 基于意图的模板回复
            String templateResponse = generateTemplateResponse(
                analysis.getIntent().getIntent(), analysis.getSentiment().getSentiment()
            );
            
            response.setContent(templateResponse);
            response.setSource("TEMPLATE");
            response.setConfidence(analysis.getIntent().getConfidence());
        }
        
        // 添加问候语
        if ("GREETING".equals(analysis.getIntent().getIntent())) {
            response.setContent("您好！我是智能客服小体，很高兴为您服务！" + 
                              (response.getContent() != null ? "\n" + response.getContent() : ""));
        }
        
        // 添加转人工提示
        if (analysis.getKnowledgeMatch() != null && analysis.getKnowledgeMatch().getScore() < 0.6) {
            response.setContent(response.getContent() + 
                              "\n\n如果还有疑问，请告诉我更多细节，或输入'转人工'联系客服人员。");
        }
        
        return response;
    }
    
    /**
     * 保存机器人回复
     */
    private ChatMessage saveBotMessage(ChatConversation conversation, String content, 
                                      MessageAnalysis analysis) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setTenantId(conversation.getTenantId());
        message.setSenderType("BOT");
        message.setSenderId("chatbot");
        message.setSenderName("智能客服");
        message.setContent(content);
        message.setContentType("TEXT");
        message.setIntent(analysis.getIntent().getIntent());
        message.setIntentConfidence(analysis.getIntent().getConfidence());
        message.setSentiment("NEUTRAL");
        message.setSentimentScore(0.0);
        
        ChatMessage savedMessage = messageRepository.save(message);
        
        // 更新对话统计
        conversation.setBotMessageCount(conversation.getBotMessageCount() + 1);
        conversationRepository.save(conversation);
        
        return savedMessage;
    }
    
    /**
     * 检查是否需要转人工
     */
    private boolean shouldEscalateToHuman(ChatConversation conversation, MessageAnalysis analysis) {
        // 规则1: 用户明确要求转人工
        if (analysis.getContent().contains("转人工") || 
            analysis.getContent().contains("人工客服")) {
            return true;
        }
        
        // 规则2: 负面情绪且问题未解决
        if ("NEGATIVE".equals(analysis.getSentiment().getSentiment()) && 
            analysis.getSentiment().getScore() < -0.5 &&
            (analysis.getKnowledgeMatch() == null || analysis.getKnowledgeMatch().getScore() < 0.6)) {
            return true;
        }
        
        // 规则3: 多次重复问题未解决
        if (conversation.getMessageCount() > 5 && 
            conversation.getBotMessageCount() >= 3 &&
            (analysis.getKnowledgeMatch() == null || analysis.getKnowledgeMatch().getScore() < 0.3)) {
            return true;
        }
        
        // 规则4: 高优先级问题
        if ("COMPLAINT".equals(analysis.getIntent().getIntent()) || 
            "URGENT".equals(conversation.getPriority())) {
            return true;
        }
        
        return false;
    }

    /**
     * 转人工处理
     */
    private void escalateToHuman(ChatConversation conversation, MessageAnalysis analysis) {
        System.out.println("转人工处理: conversationId=" + conversation.getId());
        
        conversation.setEscalated(true);
        conversation.setEscalationCount(conversation.getEscalationCount() + 1);
        conversation.setStatus("ESCALATED");
        conversationRepository.save(conversation);
        
        // 这里应该触发通知人工客服的逻辑
        // 暂时记录日志
        System.out.println("对话已转人工: " + conversation.getId() + 
                          ", 原因: " + analysis.getIntent().getIntent() + 
                          ", 情感: " + analysis.getSentiment().getSentiment());
    }

    /**
     * 知识库匹配
     */
    private KnowledgeMatchResult matchKnowledgeBase(String content, String intent) {
        // 根据意图获取相关分类的知识
        String category = mapIntentToCategory(intent);
        
        List<KnowledgeBaseItem> candidateItems = knowledgeBaseRepository
            .findByTenantIdAndCategoryAndStatus("default-tenant", category, "ACTIVE");
        
        KnowledgeMatchResult bestMatch = new KnowledgeMatchResult();
        bestMatch.setScore(0.0);
        
        for (KnowledgeBaseItem item : candidateItems) {
            double matchScore = calculateTextMatch(content, item.getQuestion());
            
            if (matchScore > bestMatch.getScore()) {
                bestMatch.setScore(matchScore);
                bestMatch.setKnowledgeId(item.getId());
                bestMatch.setTitle(item.getTitle());
                bestMatch.setContent(item.getAnswer());
                
                // 更新使用统计
                item.setUsageCount(item.getUsageCount() + 1);
                item.setLastUsedAt(LocalDateTime.now());
                knowledgeBaseRepository.save(item);
            }
        }
        
        return bestMatch;
    }
    
    /**
     * 意图映射到分类
     */
    private String mapIntentToCategory(String intent) {
        switch (intent) {
            case "MEMBERSHIP": return "MEMBERSHIP";
            case "BOOKING": return "BOOKING";
            case "PAYMENT": return "PAYMENT";
            case "FACILITY": return "FACILITY";
            case "COMPLAINT": return "GENERAL";
            default: return "GENERAL";
        }
    }
    
    /**
     * 计算文本匹配度（简化实现）
     */
    private double calculateTextMatch(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        
        String lower1 = text1.toLowerCase();
        String lower2 = text2.toLowerCase();
        
        // 简单关键词匹配
        String[] words1 = lower1.split("\\s+");
        String[] words2 = lower2.split("\\s+");
        
        int matchCount = 0;
        for (String word1 : words1) {
            if (word1.length() > 1) { // 忽略单字
                for (String word2 : words2) {
                    if (word2.contains(word1) || word1.contains(word2)) {
                        matchCount++;
                        break;
                    }
                }
            }
        }
        
        // 计算匹配度
        int totalWords = Math.max(words1.length, words2.length);
        return totalWords > 0 ? (double) matchCount / totalWords : 0.0;
    }
    
    /**
     * 生成模板回复
     */
    private String generateTemplateResponse(String intent, String sentiment) {
        Map<String, String> templateResponses = Map.of(
            "GREETING", "您好！我是智能客服小体，有什么可以帮您？",
            "MEMBERSHIP", "关于会员问题，您可以查看会员中心或联系客服人员。",
            "BOOKING", "预约相关问题，请提供具体时间和课程名称。",
            "PAYMENT", "支付问题请提供订单号，我们会尽快处理。",
            "FACILITY", "设备问题已记录，维修人员会尽快联系您。",
            "COMPLAINT", "非常抱歉给您带来不便，请描述具体情况。",
            "THANKS", "不客气，很高兴能帮助您！",
            "QUESTION", "请详细描述您的问题，我会尽力为您解答。"
        );
        
        String response = templateResponses.getOrDefault(intent, 
            "我理解您的问题，请提供更多细节以便更好地帮助您。");
        
        // 根据情感调整回复
        if ("NEGATIVE".equals(sentiment)) {
            response = "非常抱歉给您带来不好的体验，" + response;
        }
        
        return response;
    }
    
    /**
     * 获取对话历史
     */
    public List<ChatMessage> getConversationHistory(String conversationId, String tenantId) {
        return messageRepository.findByConversationIdAndTenantIdOrderByCreatedAtAsc(
            conversationId, tenantId
        );
    }
    
    /**
     * 获取活跃对话
     */
    public List<ChatConversation> getActiveConversations(String tenantId) {
        return conversationRepository.findByTenantIdAndStatus(tenantId, "ACTIVE");
    }
    
    /**
     * 关闭对话
     */
    @Transactional
    public ChatConversation closeConversation(String conversationId, String tenantId, 
                                             String resolutionSummary, Integer satisfactionScore) {
        ChatConversation conversation = conversationRepository
            .findByIdAndTenantId(conversationId, tenantId)
            .orElseThrow(() -> new RuntimeException("对话不存在"));
        
        conversation.setStatus("CLOSED");
        conversation.setResolved(true);
        conversation.setResolutionType("BOT_RESOLVED");
        conversation.setResolutionSummary(resolutionSummary);
        conversation.setSatisfactionScore(satisfactionScore);
        conversation.setClosedAt(LocalDateTime.now());
        
        // 计算对话时长
        if (conversation.getFirstMessageAt() != null) {
            long durationMs = ChronoUnit.MILLIS.between(
                conversation.getFirstMessageAt(), conversation.getClosedAt()
            );
            conversation.setTotalDurationMs(durationMs);
        }
        
        return conversationRepository.save(conversation);
    }
    
    /**
     * 添加知识库条目
     */
    @Transactional
    public KnowledgeBaseItem addKnowledgeItem(KnowledgeBaseItem item) {
        return knowledgeBaseRepository.save(item);
    }
    
    /**
     * 搜索知识库
     */
    public List<KnowledgeBaseItem> searchKnowledgeBase(String tenantId, String query, 
                                                      String category, int limit) {
        List<KnowledgeBaseItem> results = knowledgeBaseRepository.searchByQueryAndCategory(tenantId, query, category);
        if (results.size() > limit) {
            return results.subList(0, limit);
        }
        return results;
    }
    
    /**
     * 获取客服统计数据
     */
    public Map<String, Object> getChatbotStatistics(String tenantId, LocalDateTime startTime, 
                                                   LocalDateTime endTime) {
        Map<String, Object> stats = new HashMap<>();
        
        // 对话统计
        long totalConversations = conversationRepository.countByTenantIdAndCreatedAtBetween(
            tenantId, startTime, endTime
        );
        long resolvedConversations = conversationRepository.countByTenantIdAndResolvedAndCreatedAtBetween(
            tenantId, true, startTime, endTime
        );
        long escalatedConversations = conversationRepository.countByTenantIdAndEscalatedAndCreatedAtBetween(
            tenantId, true, startTime, endTime
        );
        
        // 消息统计
        long totalMessages = messageRepository.countByTenantIdAndCreatedAtBetween(
            tenantId, startTime, endTime
        );
        long botMessages = messageRepository.countByTenantIdAndSenderTypeAndCreatedAtBetween(
            tenantId, "BOT", startTime, endTime
        );
        long humanMessages = messageRepository.countByTenantIdAndSenderTypeAndCreatedAtBetween(
            tenantId, "HUMAN_AGENT", startTime, endTime
        );
        
        // 意图分布
        List<Object[]> intentDistribution = messageRepository.countByIntentAndTenantIdAndCreatedAtBetween(
            tenantId, startTime, endTime
        );
        
        // 计算指标
        double resolutionRate = totalConversations > 0 ? 
            (double) resolvedConversations / totalConversations : 0.0;
        double escalationRate = totalConversations > 0 ? 
            (double) escalatedConversations / totalConversations : 0.0;
        double botResponseRate = totalMessages > 0 ? 
            (double) botMessages / totalMessages : 0.0;
        
        stats.put("totalConversations", totalConversations);
        stats.put("resolvedConversations", resolvedConversations);
        stats.put("escalatedConversations", escalatedConversations);
        stats.put("totalMessages", totalMessages);
        stats.put("botMessages", botMessages);
        stats.put("humanMessages", humanMessages);
        stats.put("resolutionRate", Math.round(resolutionRate * 100));
        stats.put("escalationRate", Math.round(escalationRate * 100));
        stats.put("botResponseRate", Math.round(botResponseRate * 100));
        stats.put("intentDistribution", intentDistribution);
        stats.put("timeRange", startTime + " 至 " + endTime);
        
        return stats;
    }
}
