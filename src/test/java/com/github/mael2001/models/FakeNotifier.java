package com.github.mael2001.models;

import com.github.mael2001.domain.*;
import com.github.mael2001.dto.*;

public class FakeNotifier implements Notifier<NotificationRequest> {
    private final NotificationChannel channel;
    private NotificationRequest lastRequest;
    private NotificationResult resultToReturn;

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
}