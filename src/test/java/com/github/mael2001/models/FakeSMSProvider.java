package com.github.mael2001.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.mael2001.channels.SMSNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.sms.SMSConfig;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.github.mael2001.providers.sms.SMSProvider;

import lombok.Getter;
import lombok.Setter;

public class FakeSMSProvider implements SMSProvider {

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
	@Getter
	private SMSConfig providerConfig;

	@Getter
	@Setter
	private String name;

    private List<SMSNotification> sent = new ArrayList<>();

	public FakeSMSProvider() {
	}

	@Override
	public String getProviderType() {
		return NotificationChannel.SMS.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.SMS;
	}
    @Override
    public NotificationResult send(SMSNotification request) {
        sent.add(request);

        return NotificationResult.success(
                "in-memory-sms",
				NotificationChannel.SMS,
				"in-memory-sms-sent"
        );
    }

    // test helper
    public List<SMSNotification> sentSMSNotifications() {
        return List.copyOf(sent);
    }

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof SMSConfig smsConfig) {
			this.providerConfig = smsConfig;
		} else {
			throw new ValidationException("Invalid provider config type for FakeSMSProvider");
		}
	}

	@Override
	public NotificationResult sendAsync(SMSNotification request) {
		return CompletableFuture.supplyAsync(() -> send(request)).join();
	}
}
