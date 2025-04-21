package org.bits.pilani.homely.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bits.pilani.homely.entity.Setting;
import org.bits.pilani.homely.exception.HomelyException;
import org.bits.pilani.homely.repository.SettingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingService {

    private final SettingRepository settingRepository;

    public String getSetting(String key) {
        return settingRepository.findById(key)
                .map(Setting::getValue)
                .orElse(null);
    }

    public Setting updateSetting(String key, String value) {
        Setting setting = settingRepository.findById(key)
                        .orElseThrow(() -> new HomelyException("Setting not found", "SETTING_NOT_FOUND"));
        setting.setValue(value);
        return settingRepository.save(setting);
    }

    public boolean isSettingEnabled(String key) {
        return Boolean.parseBoolean(getSetting(key));
    }

    //get all
    public List<Setting> getAllSettings() {
        return settingRepository.findAll();
    }

}
