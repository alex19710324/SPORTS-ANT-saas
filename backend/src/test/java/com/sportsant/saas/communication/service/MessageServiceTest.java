package com.sportsant.saas.communication.service;

import com.sportsant.saas.communication.entity.Message;
import com.sportsant.saas.communication.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendMessage() {
        Long senderId = 1L;
        Long receiverId = 2L;
        String content = "Hello";
        
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(10L);
            return m;
        });
        
        Message result = messageService.sendMessage(senderId, receiverId, content);
        
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertFalse(result.isRead());
    }

    @Test
    public void testGetInbox() {
        Long userId = 2L;
        when(messageRepository.findByReceiverIdOrderByTimestampDesc(userId)).thenReturn(List.of(new Message()));
        
        List<Message> inbox = messageService.getInbox(userId);
        assertEquals(1, inbox.size());
    }

    @Test
    public void testMarkAsRead() {
        Long msgId = 10L;
        Message msg = new Message();
        msg.setId(msgId);
        msg.setRead(false);
        
        when(messageRepository.findById(msgId)).thenReturn(Optional.of(msg));
        
        messageService.markAsRead(msgId);
        
        assertTrue(msg.isRead());
        verify(messageRepository, times(1)).save(msg);
    }
}
