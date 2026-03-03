package com.sportsant.saas.notification.channel;

import org.springframework.stereotype.Component;

@Component
public class EmailChannel implements ChannelStrategy {
    @Override
    public boolean send(String recipient, String content) {
        System.out.println("[EMAIL] To: " + recipient + " | Body: " + content);
        return true;
    }

    @Override
    public String getChannelName() {
        return "EMAIL";
    }
}
