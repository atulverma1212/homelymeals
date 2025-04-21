package org.bits.pilani.homely.enums;

/**
 * Enum representing different categories of stock items.
 */
public enum StockCategory {
    VEG("Vegetarian"),
    NON_VEG("Non-Vegetarian"),
    DAIRY("Dairy"),
    SPICES("Spices"),
    SNACKS("Snacks");

    private final String displayName;

    StockCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}