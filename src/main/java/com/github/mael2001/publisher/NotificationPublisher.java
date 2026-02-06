package com.github.mael2001.publisher;

import com.github.mael2001.domain.NotificationEvent;

public interface NotificationPublisher {
    void publish(NotificationEvent event);
}
