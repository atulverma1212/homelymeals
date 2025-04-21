package org.bits.pilani.homely.repository;


import org.bits.pilani.homely.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
}