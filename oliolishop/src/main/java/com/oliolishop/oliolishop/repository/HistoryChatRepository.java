package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.HistoryChat;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HistoryChatRepository extends JpaRepository<HistoryChat,Long> {

    Page<HistoryChat> findBySessionIdOrderByCreatedAtAscIdAsc(String sessionId, Pageable pageable);

    Long countBySessionId(@Param("sessionId") String sessionId);


    @Query("""
            SELECT DISTINCT h.sessionId
            FROM HistoryChat h
            where h.customerId = :customerId
            """)
    String findSessionId(@Param("customerId") String customerId);

    @Transactional
    @Modifying
    @Query("DELETE FROM HistoryChat h WHERE h.sessionId = :sessionId")
    void deleteHistoryChat(@Param("sessionId") String sessionId);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM history_chat
        WHERE session_id = :sessionId
        ORDER BY created_at ASC, id ASC
        LIMIT :messageToDelete
        """, nativeQuery = true)
    void cleanupOldMessage(@Param("sessionId")String sessionId, int messageToDelete);

}
