package com.github.mael2001.models;

import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.*;
import com.github.mael2001.dto.*;

import lombok.Getter;
import lombok.Setter;

public class FakeNotifier implements Notifier<NotificationRequest> {
    private final NotificationChannel channel;
    private NotificationRequest lastRequest;
    private NotificationResult resultToReturn;

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
    @Getter
    @Setter
    private ProviderConfig providerConfig;

	@Getter
	@Setter
	private String name;



    public FakeNotifier(NotificationChannel channel, NotificationResult resultToReturn) {
        this.channel = channel;
        this.resultToReturn = resultToReturn;
    }

    @Override public NotificationChannel channel() { return channel; }

    @Override
    public NotificationResult send(NotificationRequest request) {
        this.lastRequest = request;
        return resultToReturn;
    }

    public NotificationRequest lastRequest() { return lastRequest; }

    @Override
    public String getProviderType() {
        return channel.name();
    }
}