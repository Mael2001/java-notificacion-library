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
import com.github.mael2001.exceptions.ValidationException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            throw new ValidationException("Invalid provider config type for RabbitMQEventPublisher");
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (Exception e) {
            throw new RabbitException(e);
        }
        try {
            connection.close();
        } catch (Exception e) {
            throw new RabbitException(e);
        }
    }

    @Override
    public void publish(NotificationEvent event) {
        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(providerConfig.getHost());
            factory.setPort(providerConfig.getPort());
            factory.setUsername(providerConfig.getUsername());
            factory.setPassword(providerConfig.getPassword());
            factory.setVirtualHost(providerConfig.getVirtualHost());

            this.connection = factory.newConnection("motification-rabbitmq-publisher");
            this.channel = connection.createChannel();

            // Declare exchange (topic is common for eventing)
            channel.exchangeDeclare(providerConfig.getExchange(), BuiltinExchangeType.TOPIC,
                    providerConfig.isDurable());

            if (providerConfig.isPublisherConfirms()) {
                channel.confirmSelect(); // enable publisher confirms
            }

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
                    true, // mandatory: return unroutable messages (optional)
                    props,
                    body);

            if (providerConfig.isPublisherConfirms()) {
                // blocks until broker acks publish (simple + reliable)
                channel.waitForConfirmsOrDie(5_000);
            }

        } catch (Exception e) {
            log.error("Error triggered when trying to publish event: {}", e.getMessage());
            throw new RabbitException(e);
        }
    }

    private String toJson(NotificationEvent e) {
        // minimal JSON without external deps; replace with Jackson if preferred
        String success = Boolean.toString(e.getResult().isSuccess());
        String errType = e.getResult().getErrorType() == null ? "null" : quote(e.getResult().getErrorType().name());
        String errMsg = e.getResult().getErrorMessage() == null ? "null" : quote(e.getResult().getErrorMessage());

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
        if (s == null)
            return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

}