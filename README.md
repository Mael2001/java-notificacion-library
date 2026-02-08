// ...existing code...
# Java Notification Library

Lightweight library to send notifications (Email, SMS, Push) and publish events.

## Installation
### Build

Use the included Maven wrapper:

```sh
./mvnw clean package -DskipTests
```

Files:
- [pom.xml](pom.xml)
- [Dockerfile](Dockerfile)

### Run (example)

Run the example main class:

```sh
java -cp target/java-notification-library-1.0-SNAPSHOT.jar:target/classes com.github.mael2001.examples.NotificationExamples
```

Or build and run the Docker image:

```sh
docker build -t java-notification-library .
docker run --rm java-notification-library
```

## Quick start

Create a client with the builder and register providers and configs (see [com.github.mael2001.examples.NotificationExamples](src/main/java/com/github/mael2001/examples/NotificationExamples.java)):

```java
NotificationClient client = NotificationClientBuilder.create()
    .globalConfig(new GlobalConfig())
    .retryConfig(new RetryConfig())
    .registerProviderConfiguration(new MailtrapConf(...))
    .register(NotificationChannel.EMAIL, "mailtrap", new MailtrapEmailProvider())
    .defaultProvider(NotificationChannel.EMAIL, "mailtrap")
    .build();

client.send(new EmailNotification(...));
```

## Configuration

In order to use each provider you need to instance a Config class from each of the config folder channels for example:

Provider config interfaces and examples:
- [`com.github.mael2001.config.ProviderConfig`](src/main/java/com/github/mael2001/config/ProviderConfig.java)
- Email: [`src/main/java/com/github/mael2001/config/email`](src/main/java/com/github/mael2001/config/email)
- SMS: [`src/main/java/com/github/mael2001/config/sms`](src/main/java/com/github/mael2001/config/sms)
- Push: [`src/main/java/com/github/mael2001/config/push`](src/main/java/com/github/mael2001/config/push)
- RabbitMQ: [`src/main/java/com/github/mael2001/config/rabbit/RabbitMQConfig.java`](src/main/java/com/github/mael2001/config/rabbit/RabbitMQConfig.java)

In the folder for each of the channels it exists a specific configuration for each of the specific providers, example:

- Mailtrap: [`com.github.mael2001.config.email.MailtrapConf`](src/main/java/com/github/mael2001/config/email/MailtrapConf.java)

You need to create the configuration and then provide it through the builder like this

```java
MailtrapConf DEFAULT_EMAIL_CONFIG = new MailtrapConf("test@test.com", "api-token", true, 123123);

NotificationClient client = NotificationClientBuilder.create()
    .registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
```

After registring the settings then you can add the provider you wish to use, like:

```java
MailtrapConf DEFAULT_EMAIL_CONFIG = new MailtrapConf("test@test.com", "api-token", true, 123123);

NotificationClient client = NotificationClientBuilder.create()
    .registerProviderConfiguration(DEFAULT_EMAIL_CONFIG)
	.register(NotificationChannel.EMAIL, "mailtrap", new MailtrapEmailProvider())
```

After this you will be able to use the providers


## API Reference

Key classes:
- com.github.mael2001.client.NotificationClientBuilder — builds NotificationClient instances.
- com.github.mael2001.client.NotificationClient — client interface for sending notifications.
- com.github.mael2001.client.DefaultNotificationClient — default client implementation.
- com.github.mael2001.examples.NotificationExamples — usage examples.

- com.github.mael2001.domain.NotificationRequest — base for notification requests.
- com.github.mael2001.domain.NotificationEvent — event published after send attempts.
- com.github.mael2001.domain.NotificationResult — result object returned by sends.

- com.github.mael2001.dto.NotificationChannel — enum of channels (EMAIL, SMS, PUSH).
- com.github.mael2001.dto.ErrorTypes — error type definitions.

- com.github.mael2001.channels.EmailNotification — base class for email requests (and other channel bases).

- com.github.mael2001.notifiers.* — provider interfaces and implementations.
  - com.github.mael2001.providers.push.OneSignalProvider — OneSignal push provider.
  - com.github.mael2001.providers.sms.VonageProvider — Vonage SMS provider.
  - (email providers such as Mailtrap provider)

- com.github.mael2001.publisher.NotificationPublisher — event publisher interface.
- com.github.mael2001.publisher.RabbitMQEventPublisher — RabbitMQ publisher implementation.

- com.github.mael2001.config.GlobalConfig — global client configuration.
- com.github.mael2001.config.RetryConfig — retry behavior configuration.
- com.github.mael2001.config.ProviderConfig — provider config base and subpackages:
  - com.github.mael2001.config.email.MailtrapConf
  - com.github.mael2001.config.push.OneSignalConf
  - com.github.mael2001.config.sms.VonageConf
  - com.github.mael2001.config.rabbit.RabbitMQConfig

- com.github.mael2001.exceptions.ConfigException — configuration errors.

- test helpers:
  - com.github.mael2001.models.FakeNotifier
  - com.github.mael2001.models.FakeEventPublisher


## Supported providers (included)

- Email: [`com.github.mael2001.providers.push.MailtrapEmailProvider`](src/main/java/com/github/mael2001/providers/push/MailtrapEmailProvider.java)
- Email: [`com.github.mael2001.providers.push.ResendEmailProvider`](src/main/java/com/github/mael2001/providers/push/ResendEmailProvider.java)
- Push: [`com.github.mael2001.providers.push.OneSignalProvider`](src/main/java/com/github/mael2001/providers/push/OneSignalProvider.java)
- SMS: [`com.github.mael2001.providers.sms.VonageProvider`](src/main/java/com/github/mael2001/providers/sms/VonageProvider.java)
- Event publishing: [`com.github.mael2001.publisher.RabbitMQEventPublisher`](src/main/java/com/github/mael2001/publisher/RabbitMQEventPublisher.java)

## Testing

Run unit tests:

```sh
./mvnw test
```

See tests, e.g.:
- [src/test/java/com/github/mael2001/client/DefaultNotificationClientTest.java](src/test/java/com/github/mael2001/client/DefaultNotificationClientTest.java)


## Security
Follow these best practices for managing credentials and secrets:

- Do not store secrets in code or in the repository (including commits and branches). Use .gitignore for local files.
- Load credentials from environment variables or configuration files excluded from version control (e.g., .env).
- Use a secret manager (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault, GitHub Secrets) for production and CI/CD.
- Prefer managed identities/roles (IAM, Managed Identity, Service Accounts) over static keys.
- Encrypt credentials in configuration repositories (e.g., Maven settings.xml with encrypted passwords) when necessary.
- Apply the principle of least privilege and periodic credential rotation.
- Avoid printing or logging secrets; apply masking in logs and error traces.
- Enable TLS/HTTPS for all communications that carry secrets and ensure encryption at rest.
- Integrate automatic detection in CI (secret scanning, git-secrets, truffleHog) and dependency scanning.
- If a secret is leaked, immediately revoke/rotate it and purge it from Git history (BFG, git filter-repo).

## Contribution & License

Contributions welcome. See LICENSE (Apache-2.0).
