package org.bits.pilani.homely.controller;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.dto.StockItemResponse;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.bits.pilani.homely.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockItemRestController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockItemResponse>> getAllStockItems() {
        List<StockItemResponse> items = stockService.getAllStocks();

        return ResponseEntity.ok(items);
    }

}