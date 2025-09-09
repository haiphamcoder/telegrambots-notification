package io.github.haiphamcoder.telegrambots.notification.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhotoSourceTest {

    @Test
    void testByFileId() {
        PhotoSource source = PhotoSource.byFileId("AgACAgIAAxkBAAIB");
        assertTrue(source instanceof PhotoSource.ByFileId);
        assertEquals("AgACAgIAAxkBAAIB", ((PhotoSource.ByFileId) source).fileId());
    }

    @Test
    void testByFileIdWithNull() {
        assertThrows(NullPointerException.class, () -> PhotoSource.byFileId(null));
    }

    @Test
    void testByFileIdWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> PhotoSource.byFileId(""));
        assertThrows(IllegalArgumentException.class, () -> PhotoSource.byFileId("   "));
    }

    @Test
    void testByUrl() {
        PhotoSource source = PhotoSource.byUrl("https://example.com/photo.jpg");
        assertTrue(source instanceof PhotoSource.ByUrl);
        assertEquals("https://example.com/photo.jpg", ((PhotoSource.ByUrl) source).url());
    }

    @Test
    void testByUrlWithNull() {
        assertThrows(NullPointerException.class, () -> PhotoSource.byUrl(null));
    }

    @Test
    void testByUrlWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> PhotoSource.byUrl(""));
        assertThrows(IllegalArgumentException.class, () -> PhotoSource.byUrl("   "));
    }

    @Test
    void testByUpload() {
        InputFile inputFile = InputFile.ofBytes("photo.jpg", "image/jpeg", "fake image data".getBytes());
        PhotoSource source = PhotoSource.byUpload(inputFile);
        assertTrue(source instanceof PhotoSource.ByUpload);
        assertEquals(inputFile, ((PhotoSource.ByUpload) source).inputFile());
    }

    @Test
    void testByUploadWithNull() {
        assertThrows(NullPointerException.class, () -> PhotoSource.byUpload(null));
    }

    @Test
    void testByFileIdRecord() {
        PhotoSource.ByFileId byFileId = new PhotoSource.ByFileId("AgACAgIAAxkBAAIB");
        assertEquals("AgACAgIAAxkBAAIB", byFileId.fileId());
    }

    @Test
    void testByFileIdRecordWithNull() {
        assertThrows(NullPointerException.class, () -> new PhotoSource.ByFileId(null));
    }

    @Test
    void testByFileIdRecordWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoSource.ByFileId(""));
        assertThrows(IllegalArgumentException.class, () -> new PhotoSource.ByFileId("   "));
    }

    @Test
    void testByUrlRecord() {
        PhotoSource.ByUrl byUrl = new PhotoSource.ByUrl("https://example.com/photo.jpg");
        assertEquals("https://example.com/photo.jpg", byUrl.url());
    }

    @Test
    void testByUrlRecordWithNull() {
        assertThrows(NullPointerException.class, () -> new PhotoSource.ByUrl(null));
    }

    @Test
    void testByUrlRecordWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoSource.ByUrl(""));
        assertThrows(IllegalArgumentException.class, () -> new PhotoSource.ByUrl("   "));
    }

    @Test
    void testByUploadRecord() {
        InputFile inputFile = InputFile.ofBytes("photo.jpg", "image/jpeg", "fake image data".getBytes());
        PhotoSource.ByUpload byUpload = new PhotoSource.ByUpload(inputFile);
        assertEquals(inputFile, byUpload.inputFile());
    }

    @Test
    void testByUploadRecordWithNull() {
        assertThrows(NullPointerException.class, () -> new PhotoSource.ByUpload(null));
    }
}
