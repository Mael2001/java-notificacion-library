package com.github.mael2001.providers.push;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.github.mael2001.channels.PushNotification;
import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.push.OneSignalConf;
import com.github.mael2001.domain.NotificationResult;
import com.github.mael2001.dto.ErrorTypes;
import com.github.mael2001.dto.NotificationChannel;
import com.github.mael2001.exceptions.ValidationException;
import com.onesignal.client.ApiClient;
import com.onesignal.client.ApiException;
import com.onesignal.client.Configuration;
import com.onesignal.client.api.DefaultApi;
import com.onesignal.client.auth.HttpBearerAuth;
import com.onesignal.client.model.CreateNotificationSuccessResponse;
import com.onesignal.client.model.LanguageStringMap;
import com.onesignal.client.model.Notification;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OneSignalProvider implements PushProvider {

	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
	@Getter
	@Setter
	private String name;
	@Getter
	private OneSignalConf providerConfig;

	@Override
	public String getProviderType() {
		return NotificationChannel.PUSH.name();
	}

	@Override
	public NotificationChannel channel() {
		return NotificationChannel.PUSH;
	}

	@Override
	public void setProviderConfig(ProviderConfig config) {
		if (config instanceof OneSignalConf pushConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = pushConfig;
		} else {
			throw new ValidationException("Invalid provider config type for OneSignalProvider");
		}
	}

	@Override
	public NotificationResult send(PushNotification request) {
		// Implement retry logic for async sending
		NotificationResult finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
				"notification wasn't triggered");

		log.info("Triggering {}, with {} provider", this.getProviderType().toString(), this.getName());
		// Retry loop
		for (int attempt = 1; attempt <= retryConfig.getMaxRetries(); attempt++) {
			log.info("Attempt {} to send email via {}", attempt, this.getName());
			try {
				// Send email
				CreateNotificationSuccessResponse response = sendPush(request);
				// Check response for success
				log.info("Received response from {}: {}", this.getName(), response);
				if (response.getErrors() == null) {
					log.info("Push sent successfully via {} on attempt {}", this.getName(), attempt);
					return NotificationResult.success(this.getName(), channel(), "Push sent successfully");
				} else {
					log.error("Attempt {} failed: id: {} errors: {}", attempt,
							response.getId(), response.getErrors());
					finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER,
							response.getId().toString(), channel());
				}
				// Wait before next retry
				TimeUnit.MILLISECONDS.sleep(retryConfig.getRetryIntervalMillis());
			} catch (Exception ex) {
				log.error("Attempt {} threw an exception: {}", attempt, ex.getMessage());
				finalResult = NotificationResult.failure(this.name, ErrorTypes.PROVIDER, ex.getMessage(), channel());
			}
		}
		return finalResult;
	}

	@Override
	public NotificationResult sendAsync(PushNotification request) {
		// Implement retry logic for async sending
		log.info("Triggering {}, with {} provider", this.getProviderType().toString(), this.getName());

		CompletableFuture<NotificationResult> future = CompletableFuture.supplyAsync(() -> {
			return this.send(request);
		}).exceptionally(ex -> {
			log.error("Failed to send email async via {}: {}", this.getName(), ex.getMessage());
			return NotificationResult.failure(this.getName(), ErrorTypes.UNKNOWN, ex.getMessage(), channel());
		});
		return future.join();
	}

	private CreateNotificationSuccessResponse sendPush(PushNotification request) throws ApiException{
		// Creating the client
		log.info("Creating onesignal client");

		// Separate Provider Config
		OneSignalConf config = this.getProviderConfig();

		// Create default api client
		ApiClient defaultClient = Configuration.getDefaultApiClient()
				.setDebugging(config.isDebug())
				.setBasePath(config.getApiUrl())
				.setReadTimeout(this.globalConfig.getReadTimeout())
				.setConnectTimeout(this.globalConfig.getConnectionTimeout());

		log.info("Onesignal client created");

		// Add authentication
		HttpBearerAuth restApiAuth = (HttpBearerAuth) defaultClient.getAuthentication("rest_api_key");
		restApiAuth.setBearerToken(config.getApiKey());

		// Create Notification to send
		Notification notification = new Notification();
		notification.setAppId(config.getAppId());
		notification.setIsChrome(true);
		notification.setIsAnyWeb(true);
		notification.setIsSafari(true);
		notification.setIsAndroid(true);
		notification.setIsIos(true);
		notification.setIncludedSegments(Arrays.asList(new String[] { request.getRecipientDeviceToken() }));

		LanguageStringMap headerStringMap = new LanguageStringMap();
		headerStringMap.en(request.getTitle());
		headerStringMap.es(request.getTitle());

		LanguageStringMap contentStringMap = new LanguageStringMap();
		contentStringMap.en(request.getMessage());
		contentStringMap.es(request.getMessage());

		notification.setExternalId(request.getRecipientDeviceToken());
		notification.setContents(contentStringMap);
		notification.setHeadings(headerStringMap);

		// Create api Client
		DefaultApi apiClient = new DefaultApi(defaultClient);

		// Sending the request
		return apiClient.createNotification(notification);
	}
}
