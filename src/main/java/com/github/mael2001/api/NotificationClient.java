package com.github.mael2001.api;

import com.github.mael2001.domain.NotificationRequest;
import com.github.mael2001.domain.NotificationResult;

public interface NotificationClient {

    NotificationResult send(NotificationRequest request);

}
