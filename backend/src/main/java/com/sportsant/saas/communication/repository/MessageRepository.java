package com.sportsant.saas.communication.repository;

import com.sportsant.saas.communication.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderByTimestampDesc(Long receiverId);
    List<Message> findBySenderIdOrderByTimestampDesc(Long senderId);
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}
