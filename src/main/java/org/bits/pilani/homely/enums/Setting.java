package org.bits.pilani.homely.enums;

public enum Setting {

    LOW_STOCK_THRESHOLD("low_stock_threshold"),
    SMS_NOTIFICATION_CUSTOMER("sms_notification_to_customer");

    private final String key;

    Setting(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
