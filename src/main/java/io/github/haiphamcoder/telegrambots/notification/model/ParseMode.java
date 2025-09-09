package io.github.haiphamcoder.telegrambots.notification.model;

/**
 * Represents the parse mode for Telegram message formatting.
 * Each parse mode corresponds to a different markup language supported by Telegram.
 */
public enum ParseMode {
    /**
     * HTML parse mode - supports HTML tags like &lt;b&gt;, &lt;i&gt;, &lt;code&gt;, etc.
     */
    HTML,
    
    /**
     * Markdown parse mode - supports basic Markdown syntax like **bold**, _italic_, etc.
     * This is the legacy Markdown support in Telegram.
     */
    MARKDOWN,
    
    /**
     * MarkdownV2 parse mode - supports enhanced Markdown syntax with more features
     * and stricter escaping requirements.
     */
    MARKDOWN_V2
}
