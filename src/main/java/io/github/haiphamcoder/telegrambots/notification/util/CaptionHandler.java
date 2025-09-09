package io.github.haiphamcoder.telegrambots.notification.util;

import io.github.haiphamcoder.telegrambots.notification.model.CaptionStrategy;
import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling caption text that may exceed Telegram's 1024 character limit.
 * 
 * @since 1.0.0-SNAPSHOT
 */
public class CaptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CaptionHandler.class);
    private static final int MAX_CAPTION_LENGTH = 1024;
    
    /**
     * Result of caption processing containing the caption and any remaining text.
     */
    public record CaptionResult(String caption, String remainingText) {
        public boolean hasRemainingText() {
            return remainingText != null && !remainingText.trim().isEmpty();
        }
    }
    
    /**
     * Processes a caption according to the specified strategy.
     * 
     * @param caption the original caption text
     * @param strategy the caption handling strategy
     * @param parseMode the parse mode for splitting
     * @return the caption result
     * @throws IllegalArgumentException if caption exceeds limit and strategy is ERROR
     */
    public static CaptionResult processCaption(String caption, CaptionStrategy strategy, ParseMode parseMode) {
        if (caption == null || caption.trim().isEmpty()) {
            return new CaptionResult(null, null);
        }
        
        if (caption.length() <= MAX_CAPTION_LENGTH) {
            return new CaptionResult(caption, null);
        }
        
        logger.warn("Caption length {} exceeds maximum {} characters, applying strategy: {}", 
                   caption.length(), MAX_CAPTION_LENGTH, strategy);
        
        return switch (strategy) {
            case TRUNCATE -> handleTruncate(caption);
            case SEND_REST_AS_MESSAGE -> handleSendRestAsMessage(caption, parseMode);
            case ERROR -> handleError(caption);
        };
    }
    
    /**
     * Handles caption truncation strategy.
     * 
     * @param caption the original caption
     * @return the truncated caption result
     */
    private static CaptionResult handleTruncate(String caption) {
        String truncated = caption.substring(0, MAX_CAPTION_LENGTH);
        logger.debug("Truncated caption from {} to {} characters", caption.length(), truncated.length());
        return new CaptionResult(truncated, null);
    }
    
    /**
     * Handles send rest as message strategy.
     * 
     * @param caption the original caption
     * @param parseMode the parse mode for splitting
     * @return the caption result with remaining text
     */
    private static CaptionResult handleSendRestAsMessage(String caption, ParseMode parseMode) {
        // Find a good split point within the limit
        int splitPoint = findGoodSplitPoint(caption, MAX_CAPTION_LENGTH, parseMode);
        
        String captionPart = caption.substring(0, splitPoint).trim();
        String remainingPart = caption.substring(splitPoint).trim();
        
        logger.debug("Split caption at position {}: caption={} chars, remaining={} chars", 
                   splitPoint, captionPart.length(), remainingPart.length());
        
        return new CaptionResult(captionPart, remainingPart);
    }
    
    /**
     * Handles error strategy by throwing an exception.
     * 
     * @param caption the original caption
     * @return never returns (throws exception)
     * @throws IllegalArgumentException always
     */
    private static CaptionResult handleError(String caption) {
        throw new IllegalArgumentException(
            String.format("Caption length %d exceeds maximum %d characters", 
                         caption.length(), MAX_CAPTION_LENGTH));
    }
    
    /**
     * Finds a good split point within the specified limit.
     * Tries to split at sentence boundaries, then word boundaries, then character boundaries.
     * 
     * @param text the text to split
     * @param maxLength the maximum length for the first part
     * @param parseMode the parse mode for context
     * @return the split point
     */
    private static int findGoodSplitPoint(String text, int maxLength, ParseMode parseMode) {
        if (maxLength >= text.length()) {
            return text.length();
        }
        
        // Try to split at sentence boundaries first
        int sentenceEnd = findLastSentenceEnd(text, maxLength);
        if (sentenceEnd > maxLength * 0.8) { // At least 80% of max length
            return sentenceEnd;
        }
        
        // Try to split at word boundaries
        int wordEnd = findLastWordEnd(text, maxLength);
        if (wordEnd > maxLength * 0.8) { // At least 80% of max length
            return wordEnd;
        }
        
        // Try to split at parse mode aware boundaries
        int parseModeEnd = findParseModeAwareSplit(text, maxLength, parseMode);
        if (parseModeEnd > maxLength * 0.8) { // At least 80% of max length
            return parseModeEnd;
        }
        
        // Fall back to character boundary
        return maxLength;
    }
    
    /**
     * Finds the last sentence end within the limit.
     * 
     * @param text the text to search
     * @param maxLength the maximum length
     * @return the position of the last sentence end, or maxLength if not found
     */
    private static int findLastSentenceEnd(String text, int maxLength) {
        int lastSentenceEnd = -1;
        for (int i = 0; i < Math.min(maxLength, text.length()); i++) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                // Check if it's followed by whitespace or end of string
                if (i + 1 >= text.length() || Character.isWhitespace(text.charAt(i + 1))) {
                    lastSentenceEnd = i + 1;
                }
            }
        }
        return lastSentenceEnd > 0 ? lastSentenceEnd : maxLength;
    }
    
    /**
     * Finds the last word end within the limit.
     * 
     * @param text the text to search
     * @param maxLength the maximum length
     * @return the position of the last word end, or maxLength if not found
     */
    private static int findLastWordEnd(String text, int maxLength) {
        int lastWordEnd = -1;
        for (int i = 0; i < Math.min(maxLength, text.length()); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                lastWordEnd = i;
            }
        }
        return lastWordEnd > 0 ? lastWordEnd : maxLength;
    }
    
    /**
     * Finds a parse mode aware split point.
     * 
     * @param text the text to split
     * @param maxLength the maximum length
     * @param parseMode the parse mode
     * @return the split point
     */
    private static int findParseModeAwareSplit(String text, int maxLength, ParseMode parseMode) {
        // For now, just use word boundary splitting
        // In the future, this could be enhanced to be aware of HTML tags, Markdown syntax, etc.
        return findLastWordEnd(text, maxLength);
    }
    
    /**
     * Gets the maximum caption length allowed by Telegram.
     * 
     * @return the maximum caption length
     */
    public static int getMaxCaptionLength() {
        return MAX_CAPTION_LENGTH;
    }
}
