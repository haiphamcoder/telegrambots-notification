# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-SNAPSHOT] - 2025-09-09

### Added

- Initial release of telegrambots-notification library
- Support for multiple Telegram bots with individual configurations
- HTML template system with predefined templates for different severity levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- Automatic message splitting for messages longer than 4096 characters
- Intelligent retry logic with exponential backoff for rate limiting (HTTP 429)
- Comprehensive exception hierarchy for different error types
- HTTP proxy support per bot configuration
- Stateless design suitable for serverless environments
- Builder pattern for NotificationMessage construction
- Support for action buttons in notifications
- Context data formatting and HTML escaping
- JUnit 5 test suite with 38 tests
- Complete Javadoc documentation
- Maven build with source and javadoc JAR generation
- Fat JAR support via Maven Shade plugin

### Features

- **Core Service**: `TelegramNotificationService` for sending notifications
- **Bot Management**: `BotConfigProvider` interface with `StaticBotConfigProvider` implementation
- **Message Formatting**: `NotificationFormatter` with `DefaultHtmlNotificationFormatter`
- **HTTP Client**: `TelegramHttpClient5` using Apache HttpClient 5
- **Message Splitting**: `MessageSplitter` for safe HTML message splitting
- **Retry Logic**: `RetryPolicy` with configurable retry strategies
- **Exception Handling**: Comprehensive exception hierarchy for different error scenarios
- **Template System**: Pre-built HTML templates with customizable formatting

### Dependencies

- Java 17+
- Apache HttpClient 5.5
- Gson 2.10.1
- SLF4J API 2.0.13
- Telegram Bot API Client 9.1.0
- JUnit 5.10.1 (test scope)

---
