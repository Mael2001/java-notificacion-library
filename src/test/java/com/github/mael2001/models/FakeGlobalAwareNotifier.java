package com.github.mael2001.models;

import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.dto.Notifier;

public class FakeGlobalAwareNotifier implements Notifier<NotificationRequest>, GlobalConfigAware {

    private final NotificationChannel channel;
    public GlobalConfig received;

    public FakeGlobalAwareNotifier(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override public void setGlobalConfig(GlobalConfig config) { this.received = config; }
    @Override public NotificationChannel channel() { return channel; }

    @Override
    public NotificationResult send(NotificationRequest request) {
        return NotificationResult.success("aware", this.channel, "ok");
    }
}