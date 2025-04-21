package org.bits.pilani.homely.dto;


import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String customerId;
    private String customerName;
    private List<OrderItemRequest> items;
}

