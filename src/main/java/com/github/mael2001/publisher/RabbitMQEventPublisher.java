package com.github.mael2001.publisher;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import com.github.mael2001.config.rabbit.RabbitMQConfig;
import com.github.mael2001.domain.NotificationEvent;
import com.github.mael2001.exceptions.RabbitException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQEventPublisher implements NotificationPublisher, AutoCloseable {
	// Implementation for publishing events to RabbitMQ would go here
    private final RabbitMQConfig cfg;
    private final Connection connection;
    private final Channel channel;


    public RabbitMQEventPublisher(RabbitMQConfig cfg) throws RabbitException {
        this.cfg = cfg;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(cfg.getHost());
            factory.setPort(cfg.getPort());
            factory.setUsername(cfg.getUsername());
            factory.setPassword(cfg.getPassword());
            factory.setVirtualHost(cfg.getVirtualHost());

            this.connection = factory.newConnection("motification-rabbitmq-publisher");
            this.channel = connection.createChannel();

            // Declare exchange (topic is common for eventing)
            channel.exchangeDeclare(cfg.getExchange(), BuiltinExchangeType.TOPIC, cfg.isDurable());

            if (cfg.isPublisherConfirms()) {
                channel.confirmSelect(); // enable publisher confirms
            }

        } catch (Exception e) {
            throw new RabbitException("Failed to initialize RabbitMQ publisher", e);
        }
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
                    cfg.getExchange(),
                    cfg.getRoutingKey(),
                    true,   // mandatory: return unroutable messages (optional)
                    props,
                    body
            );

            if (cfg.isPublisherConfirms()) {
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

    @Override
    public void close() {
        try { channel.close(); } catch (Exception ignored) {}
        try { connection.close(); } catch (Exception ignored) {}
    }
}