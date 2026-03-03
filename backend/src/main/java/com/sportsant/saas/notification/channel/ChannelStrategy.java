package com.sportsant.saas.notification.channel;

public interface ChannelStrategy {
    boolean send(String recipient, String content);
    String getChannelName();
}
