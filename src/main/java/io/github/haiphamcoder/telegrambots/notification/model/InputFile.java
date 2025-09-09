package io.github.haiphamcoder.telegrambots.notification.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a file to be uploaded to Telegram Bot API.
 * 
 * @since 1.0.0-SNAPSHOT
 */
public final class InputFile {
    private final String filename;
    private final String contentType;
    private final byte[] data;
    private final InputStream inputStream;
    private final long length;
    
    private InputFile(String filename, String contentType, byte[] data, InputStream inputStream, long length) {
        this.filename = Objects.requireNonNull(filename, "Filename cannot be null");
        this.contentType = contentType;
        this.data = data;
        this.inputStream = inputStream;
        this.length = length;
    }
    
    /**
     * Creates an InputFile from byte array.
     * 
     * @param filename the filename
     * @param contentType the MIME content type
     * @param data the file data
     * @return a new InputFile instance
     */
    public static InputFile ofBytes(String filename, String contentType, byte[] data) {
        Objects.requireNonNull(data, "Data cannot be null");
        return new InputFile(filename, contentType, data, null, data.length);
    }
    
    /**
     * Creates an InputFile from InputStream.
     * 
     * @param filename the filename
     * @param contentType the MIME content type
     * @param inputStream the input stream
     * @param length the length of the stream
     * @return a new InputFile instance
     */
    public static InputFile ofStream(String filename, String contentType, InputStream inputStream, long length) {
        Objects.requireNonNull(inputStream, "InputStream cannot be null");
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative");
        }
        return new InputFile(filename, contentType, null, inputStream, length);
    }
    
    /**
     * Creates an InputFile from Path.
     * 
     * @param path the file path
     * @param contentType the MIME content type
     * @return a new InputFile instance
     * @throws IOException if the file cannot be read
     */
    public static InputFile ofPath(Path path, String contentType) throws IOException {
        Objects.requireNonNull(path, "Path cannot be null");
        byte[] data = Files.readAllBytes(path);
        String filename = path.getFileName().toString();
        return new InputFile(filename, contentType, data, null, data.length);
    }
    
    /**
     * Creates an InputFile from File.
     * 
     * @param file the file
     * @param contentType the MIME content type
     * @return a new InputFile instance
     * @throws IOException if the file cannot be read
     */
    public static InputFile ofFile(File file, String contentType) throws IOException {
        Objects.requireNonNull(file, "File cannot be null");
        return ofPath(file.toPath(), contentType);
    }
    
    /**
     * Gets the filename.
     * 
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Gets the content type.
     * 
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Gets the file data as byte array.
     * 
     * @return the file data, or null if this InputFile uses InputStream
     */
    public byte[] getData() {
        return data;
    }
    
    /**
     * Gets the input stream.
     * 
     * @return the input stream, or null if this InputFile uses byte array
     */
    public InputStream getInputStream() {
        if (inputStream != null) {
            return inputStream;
        } else if (data != null) {
            return new ByteArrayInputStream(data);
        }
        return null;
    }
    
    /**
     * Gets the file length.
     * 
     * @return the file length in bytes
     */
    public long getLength() {
        return length;
    }
    
    /**
     * Checks if this InputFile uses byte array data.
     * 
     * @return true if using byte array, false if using InputStream
     */
    public boolean isByteArray() {
        return data != null;
    }
    
    /**
     * Checks if this InputFile uses InputStream.
     * 
     * @return true if using InputStream, false if using byte array
     */
    public boolean isInputStream() {
        return inputStream != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputFile inputFile = (InputFile) o;
        return length == inputFile.length &&
               Objects.equals(filename, inputFile.filename) &&
               Objects.equals(contentType, inputFile.contentType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(filename, contentType, length);
    }
    
    @Override
    public String toString() {
        return "InputFile{" +
               "filename='" + filename + '\'' +
               ", contentType='" + contentType + '\'' +
               ", length=" + length +
               '}';
    }
}
