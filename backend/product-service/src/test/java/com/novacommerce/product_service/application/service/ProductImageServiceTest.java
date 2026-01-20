package com.novacommerce.product_service.application.service;

import com.novacommerce.product_service.application.port.out.ProductImageStoragePort;
import com.novacommerce.product_service.application.port.out.ProductPersistencePort;
import com.novacommerce.product_service.domain.exception.ResourceNotFoundException;
import com.novacommerce.product_service.domain.model.Product;
import com.novacommerce.product_service.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductImageService Tests")
class ProductImageServiceTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @Mock
    private ProductImageStoragePort productImageStoragePort;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private ProductImageService productImageService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("1")
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .productType(ProductType.PHYSICAL)
                .categoryId("1")
                .stockQuantity(10)
                .status("ACTIVE")
                .imageUrl(null)
                .build();
    }

    @Test
    @DisplayName("Should upload product image successfully")
    void testUploadProductImageSuccess() throws IOException {
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(true);
        when(productImageStoragePort.storeImage("1", imageFile))
                .thenReturn("http://localhost:8083/images/products/1/uuid.jpg");
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        String imageUrl = productImageService.uploadProductImage("1", imageFile);

        assertNotNull(imageUrl);
        assertEquals("http://localhost:8083/images/products/1/uuid.jpg", imageUrl);
        verify(productPersistencePort, times(1)).findById("1");
        verify(productImageStoragePort, times(1)).isValidImage(imageFile);
        verify(productImageStoragePort, times(1)).storeImage("1", imageFile);
        verify(productPersistencePort, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void testUploadProductImageProductNotFound() throws IOException {
        when(productPersistencePort.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productImageService.uploadProductImage("999", imageFile);
        });

        verify(productPersistencePort, times(1)).findById("999");
        verify(productImageStoragePort, never()).isValidImage(any());
        verify(productImageStoragePort, never()).storeImage(anyString(), any());
    }

    @Test
    @DisplayName("Should throw exception when image file is invalid")
    void testUploadProductImageInvalidFile() throws IOException {
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            productImageService.uploadProductImage("1", imageFile);
        });

        verify(productPersistencePort, times(1)).findById("1");
        verify(productImageStoragePort, times(1)).isValidImage(imageFile);
        verify(productImageStoragePort, never()).storeImage(anyString(), any());
    }

    @Test
    @DisplayName("Should delete old image when replacing with new one")
    void testUploadProductImageReplaceExisting() throws IOException {
        product.setImageUrl("http://localhost:8083/images/products/1/old-uuid.jpg");
        
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(true);
        when(productImageStoragePort.storeImage("1", imageFile))
                .thenReturn("http://localhost:8083/images/products/1/new-uuid.jpg");
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        String imageUrl = productImageService.uploadProductImage("1", imageFile);

        assertEquals("http://localhost:8083/images/products/1/new-uuid.jpg", imageUrl);
        verify(productImageStoragePort, times(1))
                .deleteImage("http://localhost:8083/images/products/1/old-uuid.jpg");
        verify(productImageStoragePort, times(1)).storeImage("1", imageFile);
    }

    @Test
    @DisplayName("Should continue when old image deletion fails")
    void testUploadProductImageOldImageDeletionFails() throws IOException {
        product.setImageUrl("http://localhost:8083/images/products/1/old-uuid.jpg");
        
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(true);
        doThrow(new IOException("Delete failed"))
                .when(productImageStoragePort).deleteImage(anyString());
        when(productImageStoragePort.storeImage("1", imageFile))
                .thenReturn("http://localhost:8083/images/products/1/new-uuid.jpg");
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        String imageUrl = productImageService.uploadProductImage("1", imageFile);

        assertNotNull(imageUrl);
        assertEquals("http://localhost:8083/images/products/1/new-uuid.jpg", imageUrl);
        verify(productImageStoragePort, times(1)).storeImage("1", imageFile);
    }

    @Test
    @DisplayName("Should handle IOException during image storage")
    void testUploadProductImageStorageIOException() throws IOException {
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(true);
        when(productImageStoragePort.storeImage("1", imageFile))
                .thenThrow(new IOException("Storage failed"));

        assertThrows(IOException.class, () -> {
            productImageService.uploadProductImage("1", imageFile);
        });

        verify(productPersistencePort, times(1)).findById("1");
        verify(productImageStoragePort, times(1)).storeImage("1", imageFile);
        verify(productPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should not delete old image when product has no previous image")
    void testUploadProductImageNoPreviousImage() throws IOException {
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(true);
        when(productImageStoragePort.storeImage("1", imageFile))
                .thenReturn("http://localhost:8083/images/products/1/uuid.jpg");
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        productImageService.uploadProductImage("1", imageFile);

        verify(productImageStoragePort, never()).deleteImage(anyString());
    }

    @Test
    @DisplayName("Should not delete old image when imageUrl is empty string")
    void testUploadProductImageEmptyPreviousImageUrl() throws IOException {
        product.setImageUrl("");
        
        when(productPersistencePort.findById("1")).thenReturn(Optional.of(product));
        when(productImageStoragePort.isValidImage(imageFile)).thenReturn(true);
        when(productImageStoragePort.storeImage("1", imageFile))
                .thenReturn("http://localhost:8083/images/products/1/uuid.jpg");
        when(productPersistencePort.save(any(Product.class))).thenReturn(product);

        productImageService.uploadProductImage("1", imageFile);

        verify(productImageStoragePort, never()).deleteImage(anyString());
    }
}
