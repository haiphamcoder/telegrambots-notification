package io.github.haiphamcoder.telegrambots.notification.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InputFileTest {

    @Test
    void testOfBytes() {
        byte[] data = "test content".getBytes();
        InputFile inputFile = InputFile.ofBytes("test.txt", "text/plain", data);
        
        assertEquals("test.txt", inputFile.getFilename());
        assertEquals("text/plain", inputFile.getContentType());
        assertEquals(data.length, inputFile.getLength());
        assertTrue(inputFile.isByteArray());
        assertFalse(inputFile.isInputStream());
        assertArrayEquals(data, inputFile.getData());
    }

    @Test
    void testOfBytesWithNullData() {
        assertThrows(NullPointerException.class, () -> 
            InputFile.ofBytes("test.txt", "text/plain", null));
    }

    @Test
    void testOfStream() {
        byte[] data = "test content".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        InputFile inputFile = InputFile.ofStream("test.txt", "text/plain", inputStream, data.length);
        
        assertEquals("test.txt", inputFile.getFilename());
        assertEquals("text/plain", inputFile.getContentType());
        assertEquals(data.length, inputFile.getLength());
        assertFalse(inputFile.isByteArray());
        assertTrue(inputFile.isInputStream());
        assertNotNull(inputFile.getInputStream());
    }

    @Test
    void testOfStreamWithNullInputStream() {
        assertThrows(NullPointerException.class, () -> 
            InputFile.ofStream("test.txt", "text/plain", null, 10));
    }

    @Test
    void testOfStreamWithNegativeLength() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test".getBytes());
        assertThrows(IllegalArgumentException.class, () -> 
            InputFile.ofStream("test.txt", "text/plain", inputStream, -1));
    }

    @Test
    void testOfPath(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("test.txt");
        Files.write(filePath, "test content".getBytes());
        
        InputFile inputFile = InputFile.ofPath(filePath, "text/plain");
        
        assertEquals("test.txt", inputFile.getFilename());
        assertEquals("text/plain", inputFile.getContentType());
        assertEquals(12, inputFile.getLength());
        assertTrue(inputFile.isByteArray());
        assertArrayEquals("test content".getBytes(), inputFile.getData());
    }

    @Test
    void testOfPathWithNullPath() {
        assertThrows(NullPointerException.class, () -> 
            InputFile.ofPath(null, "text/plain"));
    }

    @Test
    void testOfFile(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("test.txt");
        Files.write(filePath, "test content".getBytes());
        File file = filePath.toFile();
        
        InputFile inputFile = InputFile.ofFile(file, "text/plain");
        
        assertEquals("test.txt", inputFile.getFilename());
        assertEquals("text/plain", inputFile.getContentType());
        assertEquals(12, inputFile.getLength());
        assertTrue(inputFile.isByteArray());
    }

    @Test
    void testOfFileWithNullFile() {
        assertThrows(NullPointerException.class, () -> 
            InputFile.ofFile(null, "text/plain"));
    }

    @Test
    void testGetInputStreamFromByteArray() {
        byte[] data = "test content".getBytes();
        InputFile inputFile = InputFile.ofBytes("test.txt", "text/plain", data);
        
        InputStream inputStream = inputFile.getInputStream();
        assertNotNull(inputStream);
        assertTrue(inputStream instanceof ByteArrayInputStream);
    }

    @Test
    void testEquals() {
        byte[] data1 = "test content".getBytes();
        byte[] data2 = "test content".getBytes();
        byte[] data3 = "different content".getBytes();
        
        InputFile inputFile1 = InputFile.ofBytes("test.txt", "text/plain", data1);
        InputFile inputFile2 = InputFile.ofBytes("test.txt", "text/plain", data2);
        InputFile inputFile3 = InputFile.ofBytes("test.txt", "text/plain", data3);
        InputFile inputFile4 = InputFile.ofBytes("different.txt", "text/plain", data1);
        
        assertEquals(inputFile1, inputFile2);
        assertNotEquals(inputFile1, inputFile3);
        assertNotEquals(inputFile1, inputFile4);
    }

    @Test
    void testHashCode() {
        byte[] data1 = "test content".getBytes();
        byte[] data2 = "test content".getBytes();
        
        InputFile inputFile1 = InputFile.ofBytes("test.txt", "text/plain", data1);
        InputFile inputFile2 = InputFile.ofBytes("test.txt", "text/plain", data2);
        
        assertEquals(inputFile1.hashCode(), inputFile2.hashCode());
    }

    @Test
    void testToString() {
        InputFile inputFile = InputFile.ofBytes("test.txt", "text/plain", "test".getBytes());
        String str = inputFile.toString();
        
        assertTrue(str.contains("test.txt"));
        assertTrue(str.contains("text/plain"));
        assertTrue(str.contains("4")); // length
    }
}
