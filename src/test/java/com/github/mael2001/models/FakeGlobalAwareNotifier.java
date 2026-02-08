package com.github.mael2001.models;

import java.util.concurrent.CompletableFuture;

import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.dto.Notifier;

import lombok.Getter;
import lombok.Setter;

public class FakeGlobalAwareNotifier implements Notifier<NotificationRequest>, GlobalConfigAware {

    private final NotificationChannel channel;
    public GlobalConfig received;

    @Getter
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

    public FakeGlobalAwareNotifier(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override
    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        this.received = globalConfig;
    }

    @Override
    public NotificationChannel channel() {
        return channel;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        return NotificationResult.success("aware", this.channel, "ok");
    }

    @Override
    public String getProviderType() {
        return "FakeProviderType";
    }

    @Override
    public NotificationResult sendAsync(NotificationRequest request) {
		return CompletableFuture.supplyAsync(() -> send(request)).join();
    }
}