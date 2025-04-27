package org.bits.pilani.homely.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.dto.StockItemRequest;
import org.bits.pilani.homely.entity.StockItem;
import org.bits.pilani.homely.repository.StockItemRepository;
import org.bits.pilani.homely.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StockItemRepository stockItemRepository;
    private final StockService stockService;


    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        stockService.populateStockItems(model);
        return "admin/dashboard";
    }



    @PostMapping("/stock/add")
    public String addStockItem(@ModelAttribute StockItemRequest stockItemRequest,
                               BindingResult result, RedirectAttributes redirectAttributes,
                               @RequestParam("image") MultipartFile image) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the input values");
            return "redirect:/admin/dashboard";
        }
        stockService.saveStockItem(stockItemRequest, image);
        return "redirect:/admin/dashboard";
    }



    @PostMapping("/stock/delete")
    public String deleteStockItem(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            StockItem item = stockItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            String itemName = item.getName();
            stockItemRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success",
                    "Item '" + itemName + "' deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete item");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/stock/update")
    public String updateStockItem(@Valid @ModelAttribute StockItem stockItem,
                                  BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the input values");
            return "redirect:/admin/dashboard";
        }
        stockService.updateStockItem(stockItem);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/stock/{id}")
    @ResponseBody
    public StockItem getStockItem(@PathVariable Long id) {
        return stockItemRepository.findById(id).orElseThrow();
    }
}

