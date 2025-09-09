package io.github.haiphamcoder.telegrambots.notification.util;

import io.github.haiphamcoder.telegrambots.notification.model.CaptionStrategy;
import io.github.haiphamcoder.telegrambots.notification.model.ParseMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaptionHandlerTest {

    @Test
    void testProcessCaptionWithNullCaption() {
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(null, CaptionStrategy.TRUNCATE, ParseMode.HTML);
        assertNull(result.caption());
        assertNull(result.remainingText());
        assertFalse(result.hasRemainingText());
    }

    @Test
    void testProcessCaptionWithEmptyCaption() {
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption("", CaptionStrategy.TRUNCATE, ParseMode.HTML);
        assertNull(result.caption());
        assertNull(result.remainingText());
        assertFalse(result.hasRemainingText());
    }

    @Test
    void testProcessCaptionWithWhitespaceCaption() {
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption("   ", CaptionStrategy.TRUNCATE, ParseMode.HTML);
        assertNull(result.caption());
        assertNull(result.remainingText());
        assertFalse(result.hasRemainingText());
    }

    @Test
    void testProcessCaptionWithinLimit() {
        String caption = "Short caption";
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(caption, CaptionStrategy.TRUNCATE, ParseMode.HTML);
        
        assertEquals(caption, result.caption());
        assertNull(result.remainingText());
        assertFalse(result.hasRemainingText());
    }

    @Test
    void testProcessCaptionTruncateStrategy() {
        String longCaption = "A".repeat(1500);
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(longCaption, CaptionStrategy.TRUNCATE, ParseMode.HTML);
        
        assertEquals(1024, result.caption().length());
        assertNull(result.remainingText());
        assertFalse(result.hasRemainingText());
        assertTrue(result.caption().startsWith("A"));
    }

    @Test
    void testProcessCaptionSendRestAsMessageStrategy() {
        String longCaption = "A".repeat(1500);
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(longCaption, CaptionStrategy.SEND_REST_AS_MESSAGE, ParseMode.HTML);
        
        assertTrue(result.caption().length() <= 1024);
        assertNotNull(result.remainingText());
        assertTrue(result.hasRemainingText());
        assertTrue(result.remainingText().length() > 0);
        assertEquals(longCaption, result.caption() + result.remainingText());
    }

    @Test
    void testProcessCaptionErrorStrategy() {
        String longCaption = "A".repeat(1500);
        assertThrows(IllegalArgumentException.class, () -> 
            CaptionHandler.processCaption(longCaption, CaptionStrategy.ERROR, ParseMode.HTML));
    }

    @Test
    void testProcessCaptionWithExactLimit() {
        String caption = "A".repeat(1024);
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(caption, CaptionStrategy.TRUNCATE, ParseMode.HTML);
        
        assertEquals(caption, result.caption());
        assertNull(result.remainingText());
        assertFalse(result.hasRemainingText());
    }

    @Test
    void testProcessCaptionWithOneOverLimit() {
        String caption = "A".repeat(1025);
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(caption, CaptionStrategy.SEND_REST_AS_MESSAGE, ParseMode.HTML);
        
        assertTrue(result.caption().length() <= 1024);
        assertNotNull(result.remainingText());
        assertTrue(result.hasRemainingText());
        assertEquals(caption, result.caption() + result.remainingText());
    }

    @Test
    void testProcessCaptionWithSentenceBoundary() {
        String caption = "First sentence. Second sentence. Third sentence.";
        // Make it longer than 1024 characters
        String longCaption = caption.repeat(50);
        
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(longCaption, CaptionStrategy.SEND_REST_AS_MESSAGE, ParseMode.HTML);
        
        assertTrue(result.caption().length() <= 1024);
        assertNotNull(result.remainingText());
        assertTrue(result.hasRemainingText());
        // Should end with a sentence boundary
        assertTrue(result.caption().endsWith("."));
    }

    @Test
    void testProcessCaptionWithWordBoundary() {
        String caption = "word ".repeat(300); // 1500 characters
        CaptionHandler.CaptionResult result = CaptionHandler.processCaption(caption, CaptionStrategy.SEND_REST_AS_MESSAGE, ParseMode.HTML);
        
        assertTrue(result.caption().length() <= 1024);
        assertNotNull(result.remainingText());
        assertTrue(result.hasRemainingText());
        // Should end with a word boundary (space) or be at max length
        assertTrue(result.caption().endsWith(" ") || result.caption().length() == 1024);
    }

    @Test
    void testProcessCaptionWithDifferentParseModes() {
        String longCaption = "A".repeat(1500);
        
        for (ParseMode parseMode : ParseMode.values()) {
            CaptionHandler.CaptionResult result = CaptionHandler.processCaption(longCaption, CaptionStrategy.SEND_REST_AS_MESSAGE, parseMode);
            
            assertTrue(result.caption().length() <= 1024);
            assertNotNull(result.remainingText());
            assertTrue(result.hasRemainingText());
        }
    }

    @Test
    void testGetMaxCaptionLength() {
        assertEquals(1024, CaptionHandler.getMaxCaptionLength());
    }

    @Test
    void testCaptionResultHasRemainingText() {
        CaptionHandler.CaptionResult result1 = new CaptionHandler.CaptionResult("caption", null);
        CaptionHandler.CaptionResult result2 = new CaptionHandler.CaptionResult("caption", "");
        CaptionHandler.CaptionResult result3 = new CaptionHandler.CaptionResult("caption", "   ");
        CaptionHandler.CaptionResult result4 = new CaptionHandler.CaptionResult("caption", "remaining");
        
        assertFalse(result1.hasRemainingText());
        assertFalse(result2.hasRemainingText());
        assertFalse(result3.hasRemainingText());
        assertTrue(result4.hasRemainingText());
    }
}
