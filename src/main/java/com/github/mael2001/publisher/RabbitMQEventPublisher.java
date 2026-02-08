package com.github.mael2001.publisher;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import com.github.mael2001.config.GlobalConfig;
import com.github.mael2001.config.ProviderConfig;
import com.github.mael2001.config.RetryConfig;
import com.github.mael2001.config.rabbit.RabbitMQConfig;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.exceptions.RabbitException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.Getter;
import lombok.Setter;

public class RabbitMQEventPublisher implements NotificationPublisher {
	// Implementation for publishing events to RabbitMQ would go here
	@Getter
	@Setter
	private GlobalConfig globalConfig;
	@Getter
	@Setter
	private RetryConfig retryConfig;
    @Getter
    private RabbitMQConfig providerConfig;
	@Getter
	@Setter
	private String name;

    private Connection connection;
    private Channel channel;

    @Override
    public void setProviderConfig(ProviderConfig config) {
		if (config instanceof RabbitMQConfig rabbitMQConfig && config.getProviderName().equals(this.getName())) {
			this.providerConfig = rabbitMQConfig;
		} else {
			throw new IllegalArgumentException("Invalid provider config type for RabbitMQEventPublisher");
		}
    }

    @Override
    public void close() {
        try { channel.close(); } catch (Exception ignored) {}
        try { connection.close(); } catch (Exception ignored) {}
    }

    public RabbitMQEventPublisher() {
    }

    @Override
    public void publish(NotificationEvent event) {
        try {
            byte[] body = toJson(event).getBytes(StandardCharsets.UTF_8);

            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .contentEncoding("utf-8")
                    .messageId(UUID.randomUUID().toString())
                    .timestamp(java.util.Date.from(Instant.now()))
                    .build();

            channel.basicPublish(
                    providerConfig.getExchange(),
                    providerConfig.getRoutingKey(),
                    true,   // mandatory: return unroutable messages (optional)
                    props,
                    body
            );

            if (providerConfig.isPublisherConfirms()) {
                // blocks until broker acks publish (simple + reliable)
                channel.waitForConfirmsOrDie(5_000);
            }

        } catch (Exception e) {
            // In a library, you can either:
            // 1) swallow/log (not recommended), or
            // 2) throw a runtime exception, or
            // 3) return a failure result upstream
            throw new RuntimeException("Failed to publish event to RabbitMQ", e);
        }
    }

    private String toJson(NotificationEvent e) {
        // minimal JSON without external deps; replace with Jackson if preferred
        String success = Boolean.toString(e.getResult().isSuccess());
        String errType = e.getResult().getErrorType() == null ? "null" : quote(e.getResult().getErrorType().name());
        String errMsg  = e.getResult().getErrorMessage() == null ? "null" : quote(e.getResult().getErrorMessage());

        return "{"
                + "\"correlationId\":" + quote(e.getId()) + ","
                + "\"channel\":" + quote(e.getChannel().name()) + ","
                + "\"provider\":" + quote(e.getProvider()) + ","
                + "\"occurredAt\":" + quote(e.getOccurredAt().toString()) + ","
                + "\"result\":{"
                    + "\"success\":" + success + ","
                    + "\"errorType\":" + errType + ","
                    + "\"errorMessage\":" + errMsg
                + "}"
            + "}";
    }

    private static String quote(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }


}