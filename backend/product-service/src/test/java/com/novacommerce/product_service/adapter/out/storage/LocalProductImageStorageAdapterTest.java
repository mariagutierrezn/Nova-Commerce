package com.novacommerce.product_service.adapter.out.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LocalProductImageStorageAdapter Tests")
class LocalProductImageStorageAdapterTest {

    private LocalProductImageStorageAdapter storageAdapter;

    @TempDir
    Path tempDir;

    private String uploadPath;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        storageAdapter = new LocalProductImageStorageAdapter();
        uploadPath = tempDir.toString();
        baseUrl = "http://localhost:8083";
        
        ReflectionTestUtils.setField(storageAdapter, "uploadPath", uploadPath);
        ReflectionTestUtils.setField(storageAdapter, "baseUrl", baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Cleanup is automatic with @TempDir
    }

    @Test
    @DisplayName("Should store image successfully")
    void testStoreImageSuccess() throws IOException {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageUrl = storageAdapter.storeImage("1", imageFile);

        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("http://localhost:8083/images/products/1/"));
        assertTrue(imageUrl.endsWith(".jpg"));
        
        // Verify file exists
        Path productDir = Paths.get(uploadPath, "1");
        assertTrue(Files.exists(productDir));
        assertTrue(Files.list(productDir).findAny().isPresent());
    }

    @Test
    @DisplayName("Should create directory if not exists")
    void testStoreImageCreatesDirectory() throws IOException {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        Path productDir = Paths.get(uploadPath, "1");
        assertFalse(Files.exists(productDir));

        storageAdapter.storeImage("1", imageFile);

        assertTrue(Files.exists(productDir));
    }

    @Test
    @DisplayName("Should handle PNG images")
    void testStoreImagePNG() throws IOException {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        String imageUrl = storageAdapter.storeImage("1", imageFile);

        assertTrue(imageUrl.endsWith(".png"));
    }

    @Test
    @DisplayName("Should handle WEBP images")
    void testStoreImageWEBP() throws IOException {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.webp",
                "image/webp",
                "test image content".getBytes()
        );

        String imageUrl = storageAdapter.storeImage("1", imageFile);

        assertTrue(imageUrl.endsWith(".webp"));
    }

    @Test
    @DisplayName("Should generate unique filenames")
    void testStoreImageUniqueFilenames() throws IOException {
        MultipartFile imageFile1 = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        MultipartFile imageFile2 = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageUrl1 = storageAdapter.storeImage("1", imageFile1);
        String imageUrl2 = storageAdapter.storeImage("1", imageFile2);

        assertNotEquals(imageUrl1, imageUrl2);
    }

    @Test
    @DisplayName("Should replace existing file with same name")
    void testStoreImageReplaceExisting() throws IOException {
        MultipartFile imageFile1 = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "first image".getBytes()
        );

        MultipartFile imageFile2 = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "second image".getBytes()
        );

        String imageUrl1 = storageAdapter.storeImage("1", imageFile1);
        String imageUrl2 = storageAdapter.storeImage("1", imageFile2);

        // Both should be stored (different UUIDs)
        assertNotEquals(imageUrl1, imageUrl2);
    }

    @Test
    @DisplayName("Should delete image successfully")
    void testDeleteImageSuccess() throws IOException {
        // First store an image
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageUrl = storageAdapter.storeImage("1", imageFile);
        
        // Verify file exists
        String relativePath = imageUrl.replace(baseUrl + "/images/products/", "");
        Path filePath = Paths.get(uploadPath, relativePath);
        assertTrue(Files.exists(filePath));

        // Delete the image
        storageAdapter.deleteImage(imageUrl);

        // Verify file is deleted
        assertFalse(Files.exists(filePath));
    }

    @Test
    @DisplayName("Should handle delete with null URL")
    void testDeleteImageNullUrl() {
        assertDoesNotThrow(() -> storageAdapter.deleteImage(null));
    }

    @Test
    @DisplayName("Should handle delete with empty URL")
    void testDeleteImageEmptyUrl() {
        assertDoesNotThrow(() -> storageAdapter.deleteImage(""));
    }

    @Test
    @DisplayName("Should handle delete of non-existent file")
    void testDeleteImageNonExistent() {
        String nonExistentUrl = "http://localhost:8083/images/products/1/nonexistent.jpg";
        
        assertDoesNotThrow(() -> storageAdapter.deleteImage(nonExistentUrl));
    }

    @Test
    @DisplayName("Should validate JPEG image")
    void testIsValidImageJPEG() {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        assertTrue(storageAdapter.isValidImage(imageFile));
    }

    @Test
    @DisplayName("Should validate JPG image")
    void testIsValidImageJPG() {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpg",
                "test image content".getBytes()
        );

        assertTrue(storageAdapter.isValidImage(imageFile));
    }

    @Test
    @DisplayName("Should validate PNG image")
    void testIsValidImagePNG() {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );

        assertTrue(storageAdapter.isValidImage(imageFile));
    }

    @Test
    @DisplayName("Should validate WEBP image")
    void testIsValidImageWEBP() {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.webp",
                "image/webp",
                "test image content".getBytes()
        );

        assertTrue(storageAdapter.isValidImage(imageFile));
    }

    @Test
    @DisplayName("Should reject null file")
    void testIsValidImageNull() {
        assertFalse(storageAdapter.isValidImage(null));
    }

    @Test
    @DisplayName("Should reject empty file")
    void testIsValidImageEmpty() {
        MultipartFile emptyFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertFalse(storageAdapter.isValidImage(emptyFile));
    }

    @Test
    @DisplayName("Should reject invalid content type")
    void testIsValidImageInvalidContentType() {
        MultipartFile pdfFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        assertFalse(storageAdapter.isValidImage(pdfFile));
    }

    @Test
    @DisplayName("Should reject file exceeding max size")
    void testIsValidImageExceedsMaxSize() {
        // Create file larger than 10MB
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        
        MultipartFile largeFile = new MockMultipartFile(
                "image",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        assertFalse(storageAdapter.isValidImage(largeFile));
    }

    @Test
    @DisplayName("Should accept file at max size limit")
    void testIsValidImageAtMaxSize() {
        // Create file exactly 10MB
        byte[] content = new byte[10 * 1024 * 1024]; // 10MB
        
        MultipartFile file = new MockMultipartFile(
                "image",
                "max.jpg",
                "image/jpeg",
                content
        );

        assertTrue(storageAdapter.isValidImage(file));
    }

    @Test
    @DisplayName("Should handle null content type")
    void testIsValidImageNullContentType() {
        MultipartFile fileWithNullContentType = new MockMultipartFile(
                "image",
                "test.jpg",
                null,
                "test content".getBytes()
        );

        assertFalse(storageAdapter.isValidImage(fileWithNullContentType));
    }

    @Test
    @DisplayName("Should handle filename without extension")
    void testStoreImageNoExtension() throws IOException {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test",
                "image/jpeg",
                "test image content".getBytes()
        );

        String imageUrl = storageAdapter.storeImage("1", imageFile);

        assertTrue(imageUrl.endsWith(".jpg")); // Default extension
    }

    @Test
    @DisplayName("Should be case insensitive for content type validation")
    void testIsValidImageCaseInsensitive() {
        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "IMAGE/JPEG",
                "test image content".getBytes()
        );

        assertTrue(storageAdapter.isValidImage(imageFile));
    }
}
