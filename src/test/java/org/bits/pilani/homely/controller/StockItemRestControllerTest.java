package org.bits.pilani.homely.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bits.pilani.homely.dto.StockItemResponse;
import org.bits.pilani.homely.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StockItemRestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockItemRestController stockItemRestController;

    private List<StockItemResponse> stockItems;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(stockItemRestController)
                .setControllerAdvice(new org.bits.pilani.homely.exception.HomelyExceptionHandler())
                .build();

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();

        // Set up stock items
        StockItemResponse item1 = new StockItemResponse();
        item1.setId(1L);
        item1.setName("Rice");
        item1.setCategory("VEG");
        item1.setQuantity(50.0);
        item1.setUnit("kg");
        item1.setPrice(new BigDecimal("100.00"));
        item1.setImageUrl("http://example.com/rice.jpg");
        item1.setInStock(true);

        StockItemResponse item2 = new StockItemResponse();
        item2.setId(2L);
        item2.setName("Chicken");
        item2.setCategory("NON_VEG");
        item2.setQuantity(20.0);
        item2.setUnit("kg");
        item2.setPrice(new BigDecimal("250.00"));
        item2.setImageUrl("http://example.com/chicken.jpg");
        item2.setInStock(true);

        stockItems = Arrays.asList(item1, item2);
    }

    @Test
    void getAllStockItems_ReturnsListOfItems() throws Exception {
        // Arrange
        when(stockService.getAllStocks()).thenReturn(stockItems);

        // Act & Assert
        mockMvc.perform(get("/api/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Rice")))
                .andExpect(jsonPath("$[0].category", is("VEG")))
                .andExpect(jsonPath("$[0].quantity", is(50.0)))
                .andExpect(jsonPath("$[0].unit", is("kg")))
                .andExpect(jsonPath("$[0].price", is(100.00)))
                .andExpect(jsonPath("$[0].imageUrl", is("http://example.com/rice.jpg")))
                .andExpect(jsonPath("$[0].inStock", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Chicken")))
                .andExpect(jsonPath("$[1].category", is("NON_VEG")));
    }

    @Test
    void getAllStockItems_EmptyList_ReturnsEmptyArray() throws Exception {
        // Arrange
        when(stockService.getAllStocks()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}