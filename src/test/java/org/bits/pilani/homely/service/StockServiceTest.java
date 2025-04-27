package org.bits.pilani.homely.service;

import org.bits.pilani.homely.dto.StockItemRequest;
import org.bits.pilani.homely.dto.StockItemResponse;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.enums.Setting;
import org.bits.pilani.homely.enums.StockCategory;
import org.bits.pilani.homely.exception.StockItemNotFoundException;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private SettingService settingService;

    @Mock
    private ImageUploadService imageUploadService;

    @Mock
    private Model model;

    @Mock
    private MultipartFile mockImage;

    @InjectMocks
    private StockService stockService;

    private StockItem testStockItem1;
    private StockItem testStockItem2;
    private StockItemRequest validStockItemRequest;

    @BeforeEach
    void setUp() {
        // Set up test stock items
        testStockItem1 = new StockItem();
        testStockItem1.setId(1L);
        testStockItem1.setName("Rice");
        testStockItem1.setQuantity(50.0);
        testStockItem1.setUnit("kg");
        testStockItem1.setCategory(StockCategory.VEG);
        testStockItem1.setPrice(new BigDecimal("100.00"));
        testStockItem1.setImageUrl("http://example.com/rice.jpg");
        testStockItem1.setLastUpdated(LocalDateTime.now());

        testStockItem2 = new StockItem();
        testStockItem2.setId(2L);
        testStockItem2.setName("Chicken");
        testStockItem2.setQuantity(20.0);
        testStockItem2.setUnit("kg");
        testStockItem2.setCategory(StockCategory.NON_VEG);
        testStockItem2.setPrice(new BigDecimal("250.00"));
        testStockItem2.setImageUrl("http://example.com/chicken.jpg");
        testStockItem2.setLastUpdated(LocalDateTime.now());

        // Set up valid stock item request
        validStockItemRequest = new StockItemRequest();
        validStockItemRequest.setName("Potato");
        validStockItemRequest.setQuantity(30.0);
        validStockItemRequest.setUnit("kg");
        validStockItemRequest.setCategory(StockCategory.VEG);
        validStockItemRequest.setPrice(new BigDecimal("50.00"));
    }

    @Test
    void populateStockItems_AddsItemsToModel() {
        // Arrange
        List<StockItem> stockItems = Arrays.asList(testStockItem1, testStockItem2);
        when(stockItemRepository.findAll()).thenReturn(stockItems);
        when(settingService.getSetting(Setting.LOW_STOCK_THRESHOLD.getKey())).thenReturn("10");

        // Act
        stockService.populateStockItems(model);

        // Assert
        verify(model).addAttribute("stockItems", stockItems);
        verify(model).addAttribute("categories", StockCategory.values());
        verify(model).addAttribute("lowThreshold", 10L);
    }

    @Test
    void saveStockItem_ValidRequest_SavesItem() throws Exception {
        // Arrange
        when(imageUploadService.uploadFile(mockImage)).thenReturn("http://example.com/potato.jpg");

        // Act
        stockService.saveStockItem(validStockItemRequest, mockImage);

        // Assert
        verify(imageUploadService).uploadFile(mockImage);
        verify(stockItemRepository).save(any(StockItem.class));
    }

    @Test
    void saveStockItem_UploadFails_HandlesException() throws Exception {
        // Arrange
        when(imageUploadService.uploadFile(mockImage)).thenThrow(new RuntimeException("Upload failed"));

        // Act
        stockService.saveStockItem(validStockItemRequest, mockImage);

        // Assert
        verify(imageUploadService).uploadFile(mockImage);
        verify(stockItemRepository, never()).save(any(StockItem.class));
    }

    @Test
    void getAllStocks_ReturnsStockItemResponses() {
        // Arrange
        List<StockItem> stockItems = Arrays.asList(testStockItem1, testStockItem2);
        when(stockItemRepository.findAll()).thenReturn(stockItems);

        // Act
        List<StockItemResponse> result = stockService.getAllStocks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify first item
        assertEquals(1L, result.get(0).getId());
        assertEquals("Rice", result.get(0).getName());
        assertEquals("VEG", result.get(0).getCategory());
        assertEquals(50.0, result.get(0).getQuantity());
        assertEquals("kg", result.get(0).getUnit());
        assertEquals(new BigDecimal("100.00"), result.get(0).getPrice());
        assertEquals("http://example.com/rice.jpg", result.get(0).getImageUrl());
        assertTrue(result.get(0).isInStock());
        
        // Verify second item
        assertEquals(2L, result.get(1).getId());
        assertEquals("Chicken", result.get(1).getName());
        assertEquals("NON_VEG", result.get(1).getCategory());
    }

    @Test
    void updateStockItem_ExistingItem_UpdatesItem() {
        // Arrange
        StockItem updateRequest = new StockItem();
        updateRequest.setId(1L);
        updateRequest.setName("Updated Rice");
        updateRequest.setQuantity(60.0);
        updateRequest.setUnit("kg");
        updateRequest.setCategory(StockCategory.VEG);
        updateRequest.setPrice(new BigDecimal("120.00"));
        
        when(stockItemRepository.findById(1L)).thenReturn(Optional.of(testStockItem1));
        
        // Act
        stockService.updateStockItem(updateRequest);
        
        // Assert
        verify(stockItemRepository).findById(1L);
        verify(stockItemRepository).save(any(StockItem.class));
        
        assertEquals("Updated Rice", testStockItem1.getName());
        assertEquals(60.0, testStockItem1.getQuantity());
        assertEquals(new BigDecimal("120.00"), testStockItem1.getPrice());
        assertNotNull(testStockItem1.getLastUpdated());
    }

    @Test
    void updateStockItem_NonExistingItem_ThrowsException() {
        // Arrange
        StockItem updateRequest = new StockItem();
        updateRequest.setId(999L);
        updateRequest.setName("Non-existent Item");
        
        when(stockItemRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        StockItemNotFoundException exception = assertThrows(StockItemNotFoundException.class, () -> {
            stockService.updateStockItem(updateRequest);
        });
        
        assertEquals("StockItem Not Found", exception.getMessage());
        verify(stockItemRepository).findById(999L);
        verify(stockItemRepository, never()).save(any(StockItem.class));
    }
}