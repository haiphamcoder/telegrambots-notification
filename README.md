# Telegrambots Notification

A comprehensive Java utility library for sending notifications via Telegram Bot API. This library provides a clean, stateless API for sending HTML-formatted notifications with support for multiple bots, proxy configuration, message splitting, and automatic retry handling.

## Features

- **Multiple Bot Support**: Configure and use multiple Telegram bots
- **Proxy Support**: HTTP proxy configuration per bot
- **HTML Templates**: Pre-built HTML templates for different severity levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- **Message Splitting**: Automatic splitting of messages longer than 4096 characters
- **Retry Logic**: Intelligent retry handling for rate limits (HTTP 429) with exponential backoff
- **Exception Mapping**: Clear exception hierarchy for different error types
- **Stateless Design**: No session management, perfect for serverless environments
- **Java 17+**: Modern Java features and performance optimizations

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.haiphamcoder</groupId>
    <artifactId>telegrambots-notification</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.haiphamcoder:telegrambots-notification:1.0.0-SNAPSHOT'
```

## Quick Start

### Basic Usage

```java
import io.github.haiphamcoder.telegrambots.notification.model.*;
import io.github.haiphamcoder.telegrambots.notification.provider.StaticBotConfigProvider;
import io.github.haiphamcoder.telegrambots.notification.service.TelegramNotificationService;
import io.github.haiphamcoder.telegrambots.notification.template.*;
import io.github.haiphamcoder.telegrambots.notification.util.RetryPolicy;

// Configure bots
var botConfigs = Map.of(
    "botA", new BotConfig()
        .setName("botA")
        .setToken("YOUR_BOT_TOKEN_A")
        .setChatId("YOUR_CHAT_ID_A"),
    "botB", new BotConfig()
        .setName("botB")
        .setToken("YOUR_BOT_TOKEN_B")
        .setChatId("YOUR_CHAT_ID_B")
        .setProxyType(Proxy.Type.HTTP)
        .setProxyHost("proxy.example.com")
        .setProxyPort(8080)
);

var provider = new StaticBotConfigProvider(botConfigs);
var formatter = new DefaultHtmlNotificationFormatter();
var retryPolicy = RetryPolicy.defaultPolicy();
var service = new TelegramNotificationService(provider, formatter, retryPolicy);

// Send notification
var message = NotificationMessage.builder()
    .severity(Severity.WARNING)
    .title("Disk Usage Alert")
    .body("Partition /data is 92% full")
    .context(Map.of(
        "host", "server-01",
        "partition", "/data",
        "usage", "92%",
        "threshold", "90%"
    ))
    .addAction(new Action("View Dashboard", "https://monitoring.example.com"))
    .build();

service.send("botA", message);

// Send with different parse modes
service.send("botA", message, ParseMode.HTML);        // HTML (default)
service.send("botA", message, ParseMode.MARKDOWN);    // Markdown
service.send("botA", message, ParseMode.MARKDOWN_V2); // MarkdownV2 (recommended)
```

### Advanced Configuration

```java
// Custom retry policy
var retryPolicy = new RetryPolicy(
    5,                          // max retries
    Duration.ofSeconds(2),      // base delay
    1.5,                        // multiplier
    Duration.ofSeconds(60)      // max delay
);

// Custom HTML templates
var customTemplates = Map.of(
    Severity.CRITICAL, "<b>🚨 CRITICAL</b> {{title}}<br/>{{body}}"
);
var formatter = new DefaultHtmlNotificationFormatter(customTemplates);

// Service with custom message limit
var service = new TelegramNotificationService(
    provider, 
    formatter, 
    retryPolicy, 
    3500  // message soft limit
);
```

## Configuration

### Bot Configuration

Each bot requires the following configuration:

```java
var botConfig = new BotConfig()
    .setName("my-bot")                    // Bot identifier
    .setToken("123456789:ABC...")         // Bot token from @BotFather
    .setChatId("@channel_name")           // Target chat (ID, username, or @channel)
    .setConnectTimeoutMs(5000)            // Connection timeout (optional)
    .setResponseTimeoutMs(10000)          // Response timeout (optional)
    .setProxyType(Proxy.Type.HTTP)        // Proxy type (optional)
    .setProxyHost("proxy.example.com")    // Proxy host (optional)
    .setProxyPort(8080)                   // Proxy port (optional)
    .setProxyUsername("user")             // Proxy username (optional)
    .setProxyPassword("pass");            // Proxy password (optional)
```

### Proxy Support

#### HTTP Proxy

```java
var botConfig = new BotConfig()
    .setName("bot-with-proxy")
    .setToken("YOUR_TOKEN")
    .setChatId("YOUR_CHAT_ID")
    .setProxyType(Proxy.Type.HTTP)
    .setProxyHost("proxy.example.com")
    .setProxyPort(8080)
    .setProxyUsername("username")
    .setProxyPassword("password");
```

#### SOCKS Proxy

For SOCKS proxy, use JVM system properties:

```bash
java -DsocksProxyHost=proxy.example.com -DsocksProxyPort=1080 -jar your-app.jar
```

## Message Templates

The library provides pre-built HTML templates for different severity levels:

### INFO

```html
<b>ℹ️ [INFO]</b> <b>{{title}}</b><br/>
{{body}}<br/><br/>
<blockquote>
<b>Context</b><br/>
{{context}}
</blockquote>
<i>Time:</i> <code>{{timestamp}}</code>
```

### WARNING

```html
<b>⚠️ [WARNING]</b> <b>{{title}}</b><br/>
<u>{{body}}</u><br/><br/>
<blockquote expandable>
<b>Context</b><br/>
{{context}}
</blockquote>
<i>Time:</i> <code>{{timestamp}}</code>
```

### ERROR

```html
<b>🛑 [ERROR]</b> <b>{{title}}</b><br/>
<code>{{errorCode}}</code>: {{body}}<br/><br/>
<pre><code class="language-json">{{context}}</code></pre>
<i>Time:</i> <code>{{timestamp}}</code>
```

### CRITICAL

```html
<b>🚨 [CRITICAL]</b> <b>{{title}}</b><br/>
{{body}}<br/><br/>
<blockquote>
<b>Immediate Action Required</b><br/>
{{context}}
</blockquote>
<i>Time:</i> <code>{{timestamp}}</code>
```

### DEBUG

```html
<b>🐞 [DEBUG]</b> <b>{{title}}</b><br/>
{{body}}<br/><br/>
<pre><code>{{context}}</code></pre>
<i>Time:</i> <code>{{timestamp}}</code>
```

## Exception Handling

The library provides a clear exception hierarchy:

```java
try {
    service.send("botA", message);
} catch (TelegramAuthException e) {
    // 401/403 - Invalid bot token or insufficient permissions
    logger.error("Authentication failed: {}", e.getDescription());
} catch (TelegramRateLimitException e) {
    // 429 - Rate limited
    logger.warn("Rate limited, retry after: {} seconds", e.getRetryAfter());
} catch (TelegramNetworkException e) {
    // Network issues (timeout, connection errors)
    logger.error("Network error: {}", e.getMessage());
} catch (TelegramHttpException e) {
    // Other HTTP errors
    logger.error("HTTP error {}: {}", e.getHttpStatusCode(), e.getDescription());
} catch (TelegramApiException e) {
    // Generic API errors
    logger.error("API error {}: {}", e.getErrorCode(), e.getDescription());
}
```

## Parse Modes

The library supports three parse modes for different markup languages:

### HTML (Default)
```java
// HTML is the default parse mode
service.send("botA", message);
service.send("botA", message, ParseMode.HTML);
```

### Markdown
```java
// Send with Markdown formatting
service.send("botA", message, ParseMode.MARKDOWN);

// Using custom Markdown formatter
var markdownFormatter = new MarkdownNotificationFormatter();
var service = new TelegramNotificationService(botConfigProvider, markdownFormatter);
service.send("botA", message);
```

### MarkdownV2
```java
// Send with MarkdownV2 formatting (recommended for new projects)
service.send("botA", message, ParseMode.MARKDOWN_V2);

// Using custom MarkdownV2 formatter
var markdownV2Formatter = new MarkdownV2NotificationFormatter();
var service = new TelegramNotificationService(botConfigProvider, markdownV2Formatter);
service.send("botA", message);
```

### Parse Mode Examples

**HTML Output:**
```html
<b>⚠️ [WARNING]</b> <b>System Alert</b><br/>
<i>Database connection lost</i><br/><br/>
<blockquote><b>Context</b><br/>
server: prod-web-01<br/>
timestamp: 2023-01-01T12:00:00Z</blockquote>
<i>Time:</i> <code>2023-01-01 12:00:00</code>
```

**Markdown Output:**
```markdown
⚠️ **[WARNING]** **System Alert**
_Database connection lost_

> **Context**
server: prod-web-01
timestamp: 2023-01-01T12:00:00Z

_Time:_ `2023-01-01 12:00:00`
```

**MarkdownV2 Output:**
```markdown
⚠️ **\[WARNING\]** **System Alert**
_Database connection lost_

> *Context*
server: prod\\-web\\-01
timestamp: 2023\\-01\\-01T12:00:00Z

_Time:_ `2023-01-01 12:00:00`
```

## Message Splitting

Messages longer than 4096 characters are automatically split with parse mode awareness:

```java
// Long message will be split into multiple parts
var longMessage = NotificationMessage.builder()
    .severity(Severity.INFO)
    .title("Long Report")
    .body("Very long content...") // > 4096 characters
    .build();

// HTML splitting (default)
service.send("botA", longMessage);

// MarkdownV2 splitting
service.send("botA", longMessage, ParseMode.MARKDOWN_V2);
// Will send: "Part 1/3\n\n[content]"
//           "Part 2/3\n\n[content]"
//           "Part 3/3\n\n[content]"
```

## Retry Policy

Configure retry behavior for rate limits:

```java
// Default policy: 2 retries, 1s base delay, 2x multiplier, 30s max delay
var defaultPolicy = RetryPolicy.defaultPolicy();

// Conservative policy: 5 retries, 2s base delay, 1.5x multiplier, 60s max delay
var conservativePolicy = RetryPolicy.conservativePolicy();

// Aggressive policy: 1 retry, 500ms base delay, 2x multiplier, 5s max delay
var aggressivePolicy = RetryPolicy.aggressivePolicy();

// Custom policy
var customPolicy = new RetryPolicy(
    3,                          // max retries
    Duration.ofSeconds(1),      // base delay
    2.0,                        // multiplier
    Duration.ofSeconds(30)      // max delay
);
```

## Custom Providers

Implement custom bot configuration providers:

```java
public class DatabaseBotConfigProvider implements BotConfigProvider {
    @Override
    public BotConfig get(String name) {
        // Load from database
        return loadFromDatabase(name);
    }
    
    @Override
    public Map<String, BotConfig> all() {
        // Load all from database
        return loadAllFromDatabase();
    }
}

var service = new TelegramNotificationService(
    new DatabaseBotConfigProvider(),
    formatter,
    retryPolicy
);
```

## Logging

The library uses SLF4J for logging. Add your preferred logging implementation:

### Logback

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>
```

### Log4j2

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.20.0</version>
</dependency>
```

## Building from Source

```bash
git clone https://github.com/haiphamcoder/telegrambots-notification.git
cd telegrambots-notification
mvn clean install
```

### Build with Shaded JAR

```bash
mvn clean package -P shaded
```

## Requirements

- Java 17 or higher
- Maven 3.6+ (for building)

## Dependencies

- Apache HttpClient 5.5
- Gson 2.10.1
- SLF4J API 2.0.13
- Telegram Bot API Client 9.1.0

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Roadmap

- [x] MarkdownV2 formatter support ✅
- [x] Markdown formatter support ✅
- [x] Parse mode selection (HTML, Markdown, MarkdownV2) ✅
- [ ] Photo and document sending capabilities
- [ ] Webhook support for receiving updates
- [ ] Metrics and monitoring integration
- [ ] Spring Boot auto-configuration

## Support

For questions, issues, or contributions, please visit the [GitHub repository](https://github.com/haiphamcoder/telegrambots-notification).
