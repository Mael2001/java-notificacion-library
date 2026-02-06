package com.github.mael2001.client;

import java.util.EnumMap;
import java.util.Map;

import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.dto.Notifier;
import com.github.mael2001.exceptions.ConfigException;
import com.github.mael2001.publisher.NotificationPublisher;

public class NotificationClientBuilder {

    private final Map<NotificationChannel, Map<String, Notifier<?>>> providers = new EnumMap<>(
            NotificationChannel.class);
    private final Map<NotificationChannel, String> defaults = new EnumMap<>(NotificationChannel.class);
    private GlobalConfig globalConfig;
    private RetryConfig retryConfig;
    private NotificationPublisher eventPublisher;

    public NotificationClientBuilder register(
            NotificationChannel channel,
            String name,
            Notifier<?> notifier) {
        providers
                .computeIfAbsent(channel, c -> new java.util.HashMap<>())
                .put(name, notifier);
        return this;
    }

    public NotificationClientBuilder defaultProvider(
            NotificationChannel channel,
            String name) {
        defaults.put(channel, name);
        return this;
    }

    public NotificationClientBuilder globalConfig(GlobalConfig config) {
        this.globalConfig = config;
        return this;
    }

    public NotificationClientBuilder retryConfig(RetryConfig config) {
        this.retryConfig = config;
        return this;
    }

    public NotificationClientBuilder eventPublisher(NotificationPublisher publisher) {
        this.eventPublisher = publisher;
        return this;
    }

    public NotificationClient build() throws ConfigException {
        if (globalConfig == null || retryConfig == null) {
            throw new ConfigException("GlobalConfig and RetryConfig are required");
        }

        // inject global && ret config into providers that support it
        for (Notifier<?> notifier : providers.values().stream().flatMap(m -> m.values().stream()).toList()) {
            if (notifier instanceof GlobalConfigAware aware) {
                aware.setGlobalConfig(globalConfig);
            }
            if (notifier instanceof RetryConfigAware aware) {
                aware.setRetryConfig(retryConfig);
            }
        }

        return new DefaultNotificationClient(providers, defaults, eventPublisher);
    }

}