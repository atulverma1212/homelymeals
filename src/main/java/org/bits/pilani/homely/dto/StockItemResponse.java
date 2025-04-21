package org.bits.pilani.homely.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockItemResponse {
    private Long id;
    private String name;
    private String category;
    private Double quantity;
    private String unit;
    private BigDecimal price;
    private String imageUrl;
    private boolean inStock;
}