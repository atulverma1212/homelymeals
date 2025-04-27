package org.bits.pilani.homely.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bits.pilani.homely.dto.StockItemRequest;
import org.bits.pilani.homely.dto.StockItemResponse;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.enums.Setting;
import org.bits.pilani.homely.enums.StockCategory;
import org.bits.pilani.homely.exception.StockItemNotFoundException;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final SettingService settingService;
    private final ImageUploadService imageUploadService;

    public void populateStockItems(Model model) {
        List<StockItem> stockItems = stockItemRepository.findAll();
        model.addAttribute("stockItems", stockItems);
        model.addAttribute("categories", StockCategory.values());

        String lowThreshold = settingService.getSetting(Setting.LOW_STOCK_THRESHOLD.getKey());
        model.addAttribute("lowThreshold", Long.valueOf(lowThreshold));
    }


    public void saveStockItem(StockItemRequest stockItemRequest, MultipartFile image) {
        try {
            String imageUrl = imageUploadService.uploadFile(image);

            StockItem stockItem = new StockItem();
            stockItem.setName(stockItemRequest.getName());
            stockItem.setQuantity(stockItemRequest.getQuantity());
            stockItem.setUnit(stockItemRequest.getUnit());
            stockItem.setCategory(stockItemRequest.getCategory());
            stockItem.setPrice(stockItemRequest.getPrice());
            stockItem.setImageUrl(imageUrl);
            stockItem.setLastUpdated(LocalDateTime.now());

            stockItemRepository.save(stockItem);
        } catch (Exception e) {
            log.error("Error saving stock item: {}", e.getMessage(), e);
        }
    }

    public List<StockItemResponse> getAllStocks() {
        List<StockItemResponse> items = stockItemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return items;
    }

    private StockItemResponse convertToDto(StockItem item) {
        StockItemResponse dto = new StockItemResponse();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setCategory(item.getCategory().toString());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setPrice(item.getPrice());
        dto.setImageUrl(item.getImageUrl());
        dto.setInStock(item.getQuantity() > 0);
        return dto;
    }

    public void updateStockItem(StockItem stockItem) {
        StockItem item = stockItemRepository.findById(stockItem.getId()).orElseThrow(() -> new StockItemNotFoundException("StockItem Not Found"));

        item.setName(stockItem.getName());
        item.setPrice(stockItem.getPrice());
        item.setCategory(stockItem.getCategory());
        item.setQuantity(stockItem.getQuantity());
        item.setLastUpdated(LocalDateTime.now());
        item.setUnit(stockItem.getUnit());
        stockItemRepository.save(item);
    }
}
