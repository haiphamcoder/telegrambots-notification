# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-SNAPSHOT] - 2025-09-09

### Added

- Initial release of telegrambots-notification library
- Support for multiple Telegram bots with individual configurations
- **Parse Mode Support**: HTML, Markdown, and MarkdownV2 formatting options
- **Markdown Support**: `MarkdownNotificationFormatter` with `MarkdownEscaper`
- **MarkdownV2 Support**: `MarkdownV2NotificationFormatter` with `MarkdownV2Escaper`
- **Template System**: Predefined templates for all parse modes and severity levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- Automatic message splitting for messages longer than 4096 characters with parse mode awareness
- Intelligent retry logic with exponential backoff for rate limiting (HTTP 429)
- Comprehensive exception hierarchy for different error types
- HTTP proxy support per bot configuration
- Stateless design suitable for serverless environments
- Builder pattern for NotificationMessage construction
- Support for action buttons in notifications
- Context data formatting and escaping for all parse modes
- JUnit 5 test suite with 78 tests
- Complete Javadoc documentation
- Maven build with source and javadoc JAR generation
- Fat JAR support via Maven Shade plugin

### Features

- **Core Service**: `TelegramNotificationService` for sending notifications with parse mode support
- **Bot Management**: `BotConfigProvider` interface with `StaticBotConfigProvider` implementation
- **Message Formatting**: `NotificationFormatter` interface with multiple implementations:
  - `DefaultHtmlNotificationFormatter` for HTML formatting
  - `MarkdownNotificationFormatter` for Markdown formatting
  - `MarkdownV2NotificationFormatter` for MarkdownV2 formatting
- **HTTP Client**: `TelegramBotApiClient` with parse mode support using Apache HttpClient 5
- **Message Splitting**: `MessageSplitter` for safe message splitting across all parse modes
- **Retry Logic**: `RetryPolicy` with configurable retry strategies
- **Exception Handling**: Comprehensive exception hierarchy for different error scenarios
- **Template System**: Pre-built templates for HTML, Markdown, and MarkdownV2 with customizable formatting
- **Escaping Utilities**: `HtmlEscaper`, `MarkdownEscaper`, and `MarkdownV2Escaper` for safe content formatting

### Dependencies

- Java 17+
- Apache HttpClient 5.5
- Gson 2.10.1
- SLF4J API 2.0.13
- Telegram Bot API Client 9.1.0
- JUnit 5.10.1 (test scope)

---
