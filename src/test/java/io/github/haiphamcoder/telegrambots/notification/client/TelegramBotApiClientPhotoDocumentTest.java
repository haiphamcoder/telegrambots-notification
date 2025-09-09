package io.github.haiphamcoder.telegrambots.notification.client;

import io.github.haiphamcoder.telegrambots.notification.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelegramBotApiClientPhotoDocumentTest {

    private BotConfig botConfig;
    private TelegramBotApiClient client;

    @BeforeEach
    void setUp() {
        botConfig = new BotConfig()
                .setName("testBot")
                .setToken("123456789:ABCdefGHIjklMNOpqrsTUVwxyz")
                .setChatId("123456789");
        client = new TelegramBotApiClient(botConfig);
    }

    @Test
    void testSendPhotoByFileId() {
        // This test would require mocking the HTTP client, which is complex
        // For now, we'll test the parameter validation
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoByFileId(null, "caption", ParseMode.HTML, false));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoByFileId("", "caption", ParseMode.HTML, false));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoByFileId("   ", "caption", ParseMode.HTML, false));
    }

    @Test
    void testSendPhotoByUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoByUrl(null, "caption", ParseMode.HTML, false));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoByUrl("", "caption", ParseMode.HTML, false));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoByUrl("   ", "caption", ParseMode.HTML, false));
    }

    @Test
    void testSendPhotoUpload() {
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendPhotoUpload(null, "caption", ParseMode.HTML, false));
    }

    @Test
    void testSendDocumentByFileId() {
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentByFileId(null, "caption", ParseMode.HTML));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentByFileId("", "caption", ParseMode.HTML));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentByFileId("   ", "caption", ParseMode.HTML));
    }

    @Test
    void testSendDocumentByUrl() {
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentByUrl(null, "caption", ParseMode.HTML));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentByUrl("", "caption", ParseMode.HTML));
        
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentByUrl("   ", "caption", ParseMode.HTML));
    }

    @Test
    void testSendDocumentUpload() {
        assertThrows(IllegalArgumentException.class, () -> 
            client.sendDocumentUpload(null, "caption", ParseMode.HTML));
    }

    @Test
    void testInputFileCreation() throws Exception {
        // Test InputFile creation methods
        byte[] data = "test image data".getBytes();
        InputFile inputFile = InputFile.ofBytes("test.jpg", "image/jpeg", data);
        
        assertEquals("test.jpg", inputFile.getFilename());
        assertEquals("image/jpeg", inputFile.getContentType());
        assertEquals(data.length, inputFile.getLength());
        assertTrue(inputFile.isByteArray());
        assertFalse(inputFile.isInputStream());
    }

    @Test
    void testPhotoSourceCreation() {
        // Test PhotoSource creation
        PhotoSource byFileId = PhotoSource.byFileId("AgACAgIAAxkBAAIB");
        assertTrue(byFileId instanceof PhotoSource.ByFileId);
        
        PhotoSource byUrl = PhotoSource.byUrl("https://example.com/photo.jpg");
        assertTrue(byUrl instanceof PhotoSource.ByUrl);
        
        InputFile inputFile = InputFile.ofBytes("photo.jpg", "image/jpeg", "data".getBytes());
        PhotoSource byUpload = PhotoSource.byUpload(inputFile);
        assertTrue(byUpload instanceof PhotoSource.ByUpload);
    }

    @Test
    void testDocumentSourceCreation() {
        // Test DocumentSource creation
        DocumentSource byFileId = DocumentSource.byFileId("BQACAgIAAxkBAAIB");
        assertTrue(byFileId instanceof DocumentSource.ByFileId);
        
        DocumentSource byUrl = DocumentSource.byUrl("https://example.com/document.pdf");
        assertTrue(byUrl instanceof DocumentSource.ByUrl);
        
        InputFile inputFile = InputFile.ofBytes("document.pdf", "application/pdf", "data".getBytes());
        DocumentSource byUpload = DocumentSource.byUpload(inputFile);
        assertTrue(byUpload instanceof DocumentSource.ByUpload);
    }

    @Test
    void testCaptionStrategyEnum() {
        // Test CaptionStrategy enum values
        assertEquals(3, CaptionStrategy.values().length);
        assertTrue(CaptionStrategy.valueOf("TRUNCATE") == CaptionStrategy.TRUNCATE);
        assertTrue(CaptionStrategy.valueOf("SEND_REST_AS_MESSAGE") == CaptionStrategy.SEND_REST_AS_MESSAGE);
        assertTrue(CaptionStrategy.valueOf("ERROR") == CaptionStrategy.ERROR);
    }
}
