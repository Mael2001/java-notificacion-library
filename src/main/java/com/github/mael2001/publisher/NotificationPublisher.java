package com.github.mael2001.publisher;

import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.spi.GlobalConfigAware;
import com.github.mael2001.spi.ProviderConfigAware;
import com.github.mael2001.spi.RetryConfigAware;
import com.github.mael2001.spi.Service;

public interface NotificationPublisher extends GlobalConfigAware, RetryConfigAware, ProviderConfigAware, Service, AutoCloseable {
    void publish(NotificationEvent event);
}
