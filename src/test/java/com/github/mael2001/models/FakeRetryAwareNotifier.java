package com.github.mael2001.models;

import com.github.mael2001.client.RetryConfigAware;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.dto.Notifier;

public class FakeRetryAwareNotifier implements Notifier<NotificationRequest>, RetryConfigAware {

    private final NotificationChannel channel;
    public RetryConfig received;

    public FakeRetryAwareNotifier(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override public void setRetryConfig(RetryConfig config) { this.received = config; }
    @Override public NotificationChannel channel() { return channel; }

    @Override
    public NotificationResult send(NotificationRequest request) {
        return NotificationResult.success("aware", this.channel, "ok");
    }
}