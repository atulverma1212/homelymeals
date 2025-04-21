package org.bits.pilani.homely.repository;

import org.bits.pilani.homely.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SettingRepository extends JpaRepository<Setting, String> {
}
