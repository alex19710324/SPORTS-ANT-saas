package com.sportsant.saas.chatbot.repository;

import com.sportsant.saas.chatbot.entity.KnowledgeBaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseItem, String> {
    
    // 按租户ID和分类查询
    List<KnowledgeBaseItem> findByTenantIdAndCategory(String tenantId, String category);
    
    // 按租户ID、分类和状态查询
    List<KnowledgeBaseItem> findByTenantIdAndCategoryAndStatus(String tenantId, String category, String status);
    
    // 按租户ID和状态查询
    List<KnowledgeBaseItem> findByTenantIdAndStatus(String tenantId, String status);
    
    // 按标签查询
    List<KnowledgeBaseItem> findByTenantIdAndTagsContaining(String tenantId, String tag);
    
    // 按关键词搜索
    @Query("SELECT k FROM KnowledgeBaseItem k WHERE k.tenantId = :tenantId " +
           "AND (k.question LIKE %:query% OR k.answer LIKE %:query% OR k.tags LIKE %:query%) " +
           "AND k.status = 'ACTIVE'")
    List<KnowledgeBaseItem> searchByQuery(
        @Param("tenantId") String tenantId,
        @Param("query") String query);
    
    // 按分类和关键词搜索
    @Query("SELECT k FROM KnowledgeBaseItem k WHERE k.tenantId = :tenantId " +
           "AND k.category = :category " +
           "AND (k.question LIKE %:query% OR k.answer LIKE %:query%) " +
           "AND k.status = 'ACTIVE' " +
           "ORDER BY k.popularityScore DESC")
    List<KnowledgeBaseItem> searchByQueryAndCategory(
        @Param("tenantId") String tenantId,
        @Param("query") String query,
        @Param("category") String category);
    
    // 获取热门知识
    List<KnowledgeBaseItem> findTop10ByTenantIdAndStatusOrderByPopularityScoreDesc(String tenantId, String status);
    
    // 获取最近更新的知识
    List<KnowledgeBaseItem> findTop10ByTenantIdAndStatusOrderByUpdatedAtDesc(String tenantId, String status);
    
    // 统计分类分布
    @Query("SELECT k.category, COUNT(k) FROM KnowledgeBaseItem k " +
           "WHERE k.tenantId = :tenantId AND k.status = 'ACTIVE' " +
           "GROUP BY k.category")
    List<Object[]> countByCategory(@Param("tenantId") String tenantId);
}
