package com.github.mael2001.publisher;

import com.github.mael2001.client.GlobalConfigAware;
import com.github.mael2001.client.ProviderConfigAware;
import com.github.mael2001.client.RetryConfigAware;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.dto.Service;

public interface NotificationPublisher extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Service, AutoCloseable {
    void publish(NotificationEvent event);
}
