package com.github.mael2001.models;

import java.util.ArrayList;
import java.util.List;

import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.publisher.NotificationPublisher;

import lombok.Getter;

public class FakeEventPublisher implements NotificationPublisher {
    @Getter final List<NotificationEvent> events = new ArrayList<>();

    @Override
    public void publish(NotificationEvent event) {
        events.add(event);
    }
}