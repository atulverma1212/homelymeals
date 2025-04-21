package org.bits.pilani.homely.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long stockItemId;
    private Double quantity;
}