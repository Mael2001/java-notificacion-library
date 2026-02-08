package com.github.mael2001.api;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ConfigException;
import com.github.mael2001.impl.DefaultNotificationClient;
import com.github.mael2001.publisher.NotificationPublisher;
import com.github.mael2001.spi.GlobalConfigAware;
import com.github.mael2001.spi.Notifier;
import com.github.mael2001.spi.ProviderConfigAware;
import com.github.mael2001.spi.RetryConfigAware;

public class NotificationClientBuilder {

    private final Map<String,  Notifier<?>> providers = new HashMap<>();
    private final Map<String, NotificationPublisher> eventPublishers = new HashMap<>();
    private final Map<NotificationChannel, String> defaults = new EnumMap<>(NotificationChannel.class);

    private GlobalConfig globalConfig;
    private RetryConfig retryConfig;
    private List<ProviderConfig> providerConfigs = new ArrayList<>();


    public static NotificationClientBuilder create() {
        return new NotificationClientBuilder();
    }

    public NotificationClientBuilder globalConfig(GlobalConfig config) {
        this.globalConfig = config;
        return this;
    }

    public NotificationClientBuilder retryConfig(RetryConfig config) {
        this.retryConfig = config;
        return this;
    }

    public NotificationClientBuilder registerProviderConfiguration(
            ProviderConfig config) {
        providerConfigs.add(config);
        return this;
    }

    public NotificationClientBuilder register(
            NotificationChannel channel,
            String name,
            Notifier<?> notifier) {
        notifier.setName(name);
        providers.putIfAbsent(name, notifier);
        return this;
    }

    public NotificationClientBuilder defaultProvider(
            NotificationChannel channel,
            String name) {
        defaults.put(channel, name);
        return this;
    }


    public NotificationClientBuilder registerEventPublisher(String name, NotificationPublisher publisher) {
        publisher.setName(name);
        this.eventPublishers.put(name, publisher);
        return this;
    }

    public NotificationClient build() throws ConfigException {
        if (globalConfig == null || retryConfig == null) {
            throw new ConfigException("GlobalConfig and RetryConfig are required");
        }

        // inject global && ret config into providers that support it
        for (Notifier<?> notifier : providers.values()) {
            if (notifier instanceof GlobalConfigAware aware) {
                aware.setGlobalConfig(globalConfig);
            }
            if (notifier instanceof RetryConfigAware aware) {
                aware.setRetryConfig(retryConfig);
            }
            if (notifier instanceof ProviderConfigAware aware) {
                // find provider config for this notifier
                ProviderConfig config = providerConfigs.stream()
                        .filter(c -> c.getProviderName().equals(notifier.getName()))
                        .findFirst()
                        .orElseThrow(() -> new ConfigException(
                                "No provider config found for provider: " + notifier.getName()));
                aware.setProviderConfig(config);
            }
            //Set default name if not set
            if (notifier.getName() == null || notifier.getName().isBlank()) {
                notifier.setName(notifier.getClass().getSimpleName());
            }
        }

        // inject global && retry config into event publishers that support it
        for (NotificationPublisher publisher : eventPublishers.values()) {
                publisher.setGlobalConfig(globalConfig);
                publisher.setRetryConfig(retryConfig);

                ProviderConfig config = providerConfigs.stream()
                        .filter(c -> c.getProviderName().equals(publisher.getName()))
                        .findFirst()
                        .orElseThrow(() -> new ConfigException(
                                "No provider config found for publisher: " + publisher.getName()));

                publisher.setProviderConfig(config);

            //Set default name if not set
            if (publisher.getName() == null || publisher.getName().isBlank()) {
                publisher.setName(publisher.getClass().getSimpleName());
            }
        }

        return new DefaultNotificationClient(providers, defaults, eventPublishers);
    }

}