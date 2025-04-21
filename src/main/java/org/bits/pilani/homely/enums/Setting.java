package org.bits.pilani.homely.enums;

public enum Setting {

    LOW_STOCK_THRESHOLD("low_stock_threshold"),
    LOW_STOCK_NOTIFICATION("low_stock_notification"),
    STOCK_NOTIFICATION_EMAIL("stock_notification_email"),
    STOCK_NOTIFICATION_PHONE("stock_notification_phone"),
    STOCK_NOTIFICATION_WHATSAPP("stock_notification_whatsapp"),
    STOCK_NOTIFICATION_SMS("stock_notification_sms");

    private final String key;

    Setting(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
