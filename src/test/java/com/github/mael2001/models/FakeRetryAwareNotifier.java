package com.github.mael2001.models;

import java.util.concurrent.CompletableFuture;

import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.spi.Notifier;
import com.github.mael2001.spi.RetryConfigAware;

import lombok.Getter;
import lombok.Setter;

public class FakeRetryAwareNotifier implements Notifier<NotificationRequest>, RetryConfigAware {

    private final NotificationChannel channel;
    public RetryConfig received;

	@Getter
	private RetryConfig retryConfig;
    @Getter
    @Setter
    private ProviderConfig providerConfig;

	@Getter
	@Setter
	private String name;


    public FakeRetryAwareNotifier(NotificationChannel channel) {
        this.channel = channel;
    }
    @Override public NotificationChannel channel() { return channel; }

    @Override
    public NotificationResult send(NotificationRequest request) {
        return NotificationResult.success("aware", this.channel, "ok");
    }

    @Override
    public String getProviderType() {
        return "FakeProviderType";
    }
    @Override
    public void setRetryConfig(RetryConfig config) {
        this.retryConfig = config;
        this.received = config;
    }
    @Override
    public NotificationResult sendAsync(NotificationRequest request) {
		return CompletableFuture.supplyAsync(() -> send(request)).join();
    }


}