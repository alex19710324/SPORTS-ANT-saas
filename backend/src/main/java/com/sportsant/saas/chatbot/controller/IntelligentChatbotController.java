package com.sportsant.saas.chatbot.controller;

import com.sportsant.saas.chatbot.entity.ChatConversation;
import com.sportsant.saas.chatbot.entity.ChatMessage;
import com.sportsant.saas.chatbot.entity.KnowledgeBaseItem;
import com.sportsant.saas.chatbot.service.IntelligentChatbotService;
import com.sportsant.saas.chatbot.service.helper.ChatRequest;
import com.sportsant.saas.chatbot.service.helper.ChatResponse;
import com.sportsant.saas.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "智能客服机器人", description = "AI智能客服对话系统")
public class IntelligentChatbotController {
    
    private final IntelligentChatbotService chatbotService;

    public IntelligentChatbotController(IntelligentChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }
    
    @PostMapping("/chat")
    @Operation(summary = "发送消息给智能客服")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('MEMBER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @RequestBody ChatRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        request.setTenantId(tenantId);
        ChatResponse response = chatbotService.processUserMessage(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/conversation/{conversationId}/history")
    @Operation(summary = "获取对话历史")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF') or hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getConversationHistory(
            @PathVariable String conversationId,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        List<ChatMessage> history = chatbotService.getConversationHistory(conversationId, tenantId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
    
    @GetMapping("/conversations/active")
    @Operation(summary = "获取活跃对话列表")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<ChatConversation>>> getActiveConversations(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        List<ChatConversation> conversations = chatbotService.getActiveConversations(tenantId);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }
    
    @PostMapping("/conversation/{conversationId}/close")
    @Operation(summary = "关闭对话")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<ChatConversation>> closeConversation(
            @PathVariable String conversationId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam String resolutionSummary,
            @RequestParam(required = false) Integer satisfactionScore) {
        ChatConversation conversation = chatbotService.closeConversation(
            conversationId, tenantId, resolutionSummary, satisfactionScore);
        return ResponseEntity.ok(ApiResponse.success(conversation, "对话已关闭"));
    }
    
    @PostMapping("/knowledge/add")
    @Operation(summary = "添加知识库条目")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<KnowledgeBaseItem>> addKnowledgeItem(
            @RequestBody KnowledgeBaseItem item,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        item.setTenantId(tenantId);
        KnowledgeBaseItem savedItem = chatbotService.addKnowledgeItem(item);
        return ResponseEntity.ok(ApiResponse.success(savedItem, "知识库条目已添加"));
    }
    
    @GetMapping("/knowledge/search")
    @Operation(summary = "搜索知识库")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<KnowledgeBaseItem>>> searchKnowledgeBase(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit) {
        List<KnowledgeBaseItem> results = chatbotService.searchKnowledgeBase(tenantId, query, category, limit);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取客服统计数据")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChatbotStatistics(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "7") int days) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        
        Map<String, Object> statistics = chatbotService.getChatbotStatistics(tenantId, startTime, endTime);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "客服系统仪表板")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChatbotDashboard(
            @RequestHeader("X-Tenant-ID") String tenantId) {
        // 这里应该返回仪表板数据
        // 暂时返回空数据
        return ResponseEntity.ok(ApiResponse.success(Map.of()));
    }
    
    @GetMapping("/intents")
    @Operation(summary = "获取意图分析")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getIntentAnalysis(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "30") int days) {
        // 这里应该返回意图分析数据
        // 暂时返回空数据
        return ResponseEntity.ok(ApiResponse.success(Map.of()));
    }
    
    @GetMapping("/escalations")
    @Operation(summary = "获取转人工记录")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<ChatConversation>>> getEscalatedConversations(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "7") int days) {
        // 这里应该返回转人工记录
        // 暂时返回空列表
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
    
    @GetMapping("/knowledge/popular")
    @Operation(summary = "获取热门知识")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<KnowledgeBaseItem>>> getPopularKnowledge(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestParam(defaultValue = "10") int limit) {
        // 这里应该返回热门知识
        // 暂时返回空列表
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
}
