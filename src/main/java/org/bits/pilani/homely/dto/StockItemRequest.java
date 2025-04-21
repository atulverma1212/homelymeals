package org.bits.pilani.homely.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.bits.pilani.homely.enums.StockCategory;

import java.math.BigDecimal;

@Data
public class StockItemRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    private Double quantity;

    @NotBlank(message = "Unit is required")
    @Size(max = 50, message = "Unit must not exceed 50 characters")
    private String unit;

    @NotNull(message = "Category is required")
    private StockCategory category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    private String imageUrl;
}