package com.sportsant.saas.notification.channel;

import org.springframework.stereotype.Component;

@Component
public class SmsChannel implements ChannelStrategy {
    @Override
    public boolean send(String recipient, String content) {
        System.out.println("[SMS] To: " + recipient + " | Msg: " + content);
        return true;
    }
    
    @Override
    public String getChannelName() {
        return "SMS";
    }
}
