package org.bits.pilani.homely.controller;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.service.SettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminSettingsController {


    private final SettingService settingService;

    @GetMapping("/settings")
    public String showSettings(Model model) {
        model.addAttribute("settings", settingService.getAllSettings());
        return "admin/settings";
    }

    @PutMapping("/setting/{name}")
    public ResponseEntity<?> updateSetting(@PathVariable String name, @RequestBody String value) {
        settingService.updateSetting(name, value);
        return ResponseEntity.ok().build();
    }
}

