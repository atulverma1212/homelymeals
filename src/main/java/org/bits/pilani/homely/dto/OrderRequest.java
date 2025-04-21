package org.bits.pilani.homely.dto;


import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long customerId;
    private List<OrderItemRequest> items;
}

