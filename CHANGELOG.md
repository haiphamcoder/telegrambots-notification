# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.3] - 2025-09-10

### Fixed

- **URL Encoding Issue**: Fixed critical URL encoding problem in `TelegramHttpClient5` that was causing emoji and special characters to display as `??` in Telegram messages
- **Form Data Encoding**: Added proper UTF-8 URL encoding for all form data parameters in both `postForm()` and `postMultipart()` methods
- **Emoji Display**: Resolved emoji display issues in MarkdownV2 and HTML parse modes by ensuring proper character encoding during API requests

### Changed

- **HTTP Client**: Enhanced `TelegramHttpClient5` with proper URL encoding using `URLEncoder.encode()` with UTF-8 charset
- **Error Handling**: Improved error handling in URL encoding with fallback to original value if encoding fails
- **Character Support**: Better support for Unicode characters, emojis, and special symbols in message content

## [0.0.2] - 2025-09-10

### Fixed

- **HTML Template Formatting**: Fixed HTML templates to remove unnecessary `\n` characters that were causing display issues with `??` symbols
- **MarkdownV2 Template Formatting**: Fixed MarkdownV2 templates by removing excessive escaping for severity labels like `[INFO]`, `[WARNING]`, etc.
- **Timestamp Escaping**: Fixed timestamp escaping in MarkdownV2 mode to use `escapeCode()` instead of `escapeText()` for proper display within backticks
- **Blockquote Structure**: Improved HTML blockquote structure for better message formatting

### Changed

- **Template Cleanup**: Cleaned up all HTML and MarkdownV2 templates for better readability and proper formatting
- **Escaping Logic**: Improved escaping logic for different content types (text vs code) in MarkdownV2

## [1.0.0-SNAPSHOT] - 2025-09-09

### Added

- Initial release of telegrambots-notification library
- Support for multiple Telegram bots with individual configurations
- **Parse Mode Support**: HTML, Markdown, and MarkdownV2 formatting options
- **Markdown Support**: `MarkdownNotificationFormatter` with `MarkdownEscaper`
- **MarkdownV2 Support**: `MarkdownV2NotificationFormatter` with `MarkdownV2Escaper`
- **Photo and Document Support**: Complete support for sending photos and documents via Telegram Bot API
- **Photo Sources**: Support for file ID, URL, and file upload sources
- **Document Sources**: Support for file ID, URL, and file upload sources
- **Caption Handling**: Automatic caption splitting for content exceeding 1024 characters
- **InputFile Model**: Flexible file representation with multiple creation methods
- **MessageId Model**: Response tracking for sent messages
- **CaptionStrategy**: Configurable strategies for handling long captions (TRUNCATE, SEND_REST_AS_MESSAGE, ERROR)
- **Template System**: Predefined templates for all parse modes and severity levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- Automatic message splitting for messages longer than 4096 characters with parse mode awareness
- Intelligent retry logic with exponential backoff for rate limiting (HTTP 429)
- Comprehensive exception hierarchy for different error types
- HTTP proxy support per bot configuration
- Stateless design suitable for serverless environments
- Builder pattern for NotificationMessage construction
- Support for action buttons in notifications
- Context data formatting and escaping for all parse modes
- JUnit 5 test suite with 151 tests
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
- **Photo and Document Support**: Complete API for sending photos and documents
  - `PhotoSource` and `DocumentSource` sealed interfaces with multiple implementations
  - `InputFile` model for flexible file representation
  - `MessageId` model for tracking sent messages
  - `CaptionHandler` utility for intelligent caption splitting
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
