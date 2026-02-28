package com.sportsant.saas.communication.service;

import com.sportsant.saas.communication.entity.Message;
import com.sportsant.saas.communication.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setRead(false);
        // timestamp set by @PrePersist
        return messageRepository.save(msg);
    }

    public List<Message> getInbox(Long userId) {
        return messageRepository.findByReceiverIdOrderByTimestampDesc(userId);
    }
    
    public List<Message> getOutbox(Long userId) {
        return messageRepository.findBySenderIdOrderByTimestampDesc(userId);
    }
    
    public long getUnreadCount(Long userId) {
        return messageRepository.findByReceiverIdAndIsReadFalse(userId).size();
    }
    
    @Transactional
    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(msg -> {
            msg.setRead(true);
            messageRepository.save(msg);
        });
    }
}
