package org.bits.pilani.homely.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.enums.Setting;
import org.bits.pilani.homely.enums.StockCategory;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final SettingService settingService;

    public void populateStockItems(Model model) {
        List<StockItem> stockItems = stockItemRepository.findAll();
        model.addAttribute("stockItems", stockItems);
        model.addAttribute("categories", StockCategory.values());

        String lowThreshold = settingService.getSetting(Setting.LOW_STOCK_THRESHOLD.getKey());
        model.addAttribute("lowThreshold", Long.valueOf(lowThreshold));
    }


}
