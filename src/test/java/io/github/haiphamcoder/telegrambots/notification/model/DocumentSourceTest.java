package io.github.haiphamcoder.telegrambots.notification.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentSourceTest {

    @Test
    void testByFileId() {
        DocumentSource source = DocumentSource.byFileId("BQACAgIAAxkBAAIB");
        assertTrue(source instanceof DocumentSource.ByFileId);
        assertEquals("BQACAgIAAxkBAAIB", ((DocumentSource.ByFileId) source).fileId());
    }

    @Test
    void testByFileIdWithNull() {
        assertThrows(NullPointerException.class, () -> DocumentSource.byFileId(null));
    }

    @Test
    void testByFileIdWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> DocumentSource.byFileId(""));
        assertThrows(IllegalArgumentException.class, () -> DocumentSource.byFileId("   "));
    }

    @Test
    void testByUrl() {
        DocumentSource source = DocumentSource.byUrl("https://example.com/document.pdf");
        assertTrue(source instanceof DocumentSource.ByUrl);
        assertEquals("https://example.com/document.pdf", ((DocumentSource.ByUrl) source).url());
    }

    @Test
    void testByUrlWithNull() {
        assertThrows(NullPointerException.class, () -> DocumentSource.byUrl(null));
    }

    @Test
    void testByUrlWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> DocumentSource.byUrl(""));
        assertThrows(IllegalArgumentException.class, () -> DocumentSource.byUrl("   "));
    }

    @Test
    void testByUpload() {
        InputFile inputFile = InputFile.ofBytes("document.pdf", "application/pdf", "fake pdf data".getBytes());
        DocumentSource source = DocumentSource.byUpload(inputFile);
        assertTrue(source instanceof DocumentSource.ByUpload);
        assertEquals(inputFile, ((DocumentSource.ByUpload) source).inputFile());
    }

    @Test
    void testByUploadWithNull() {
        assertThrows(NullPointerException.class, () -> DocumentSource.byUpload(null));
    }

    @Test
    void testByFileIdRecord() {
        DocumentSource.ByFileId byFileId = new DocumentSource.ByFileId("BQACAgIAAxkBAAIB");
        assertEquals("BQACAgIAAxkBAAIB", byFileId.fileId());
    }

    @Test
    void testByFileIdRecordWithNull() {
        assertThrows(NullPointerException.class, () -> new DocumentSource.ByFileId(null));
    }

    @Test
    void testByFileIdRecordWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new DocumentSource.ByFileId(""));
        assertThrows(IllegalArgumentException.class, () -> new DocumentSource.ByFileId("   "));
    }

    @Test
    void testByUrlRecord() {
        DocumentSource.ByUrl byUrl = new DocumentSource.ByUrl("https://example.com/document.pdf");
        assertEquals("https://example.com/document.pdf", byUrl.url());
    }

    @Test
    void testByUrlRecordWithNull() {
        assertThrows(NullPointerException.class, () -> new DocumentSource.ByUrl(null));
    }

    @Test
    void testByUrlRecordWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new DocumentSource.ByUrl(""));
        assertThrows(IllegalArgumentException.class, () -> new DocumentSource.ByUrl("   "));
    }

    @Test
    void testByUploadRecord() {
        InputFile inputFile = InputFile.ofBytes("document.pdf", "application/pdf", "fake pdf data".getBytes());
        DocumentSource.ByUpload byUpload = new DocumentSource.ByUpload(inputFile);
        assertEquals(inputFile, byUpload.inputFile());
    }

    @Test
    void testByUploadRecordWithNull() {
        assertThrows(NullPointerException.class, () -> new DocumentSource.ByUpload(null));
    }
}
