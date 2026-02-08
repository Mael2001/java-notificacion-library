package com.github.mael2001.models;

import java.util.ArrayList;
import java.util.List;

import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.publisher.NotificationPublisher;

import lombok.Data;

@Data
public class FakeEventPublisher implements NotificationPublisher {
    final List<NotificationEvent> events = new ArrayList<>();
	private GlobalConfig globalConfig;
	private RetryConfig retryConfig;
    private ProviderConfig providerConfig;
	private String name;

    @Override
    public void publish(NotificationEvent event) {
        events.add(event);
    }

    @Override
    public void close() throws Exception {
        // No resources to close in this fake implementation
    }
}