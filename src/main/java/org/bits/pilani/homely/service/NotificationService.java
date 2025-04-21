package org.bits.pilani.homely.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bits.pilani.homely.enums.Setting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${aws.sns.senderId}")
    private String senderId;

    @Value("${aws.sns.region}")
    private String region;

    private final SnsClient snsClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final SettingService settingService;

    public void sendOrderNotification(String message) {
        messagingTemplate.convertAndSend("/topic/admin-notifications", message);
    }

    public void sendSms(String phoneNumber, String message) {
        if(! settingService.isSettingEnabled(Setting.SMS_NOTIFICATION_CUSTOMER.getKey())) {
            log.info("SMS notifications are disabled. Not sending SMS to {}", phoneNumber);
            return;
        }
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .messageAttributes(java.util.Map.of(
                            "AWS.SNS.SMS.SenderID",
                            software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                .stringValue(senderId)
                                .dataType("String")
                                .build(),
                            "AWS.SNS.SMS.SMSType",
                            software.amazon.awssdk.services.sns.model.MessageAttributeValue.builder()
                                .stringValue("Promotional")
                                .dataType("String")
                                .build()
                    ))
                    .build();

            PublishResponse response = snsClient.publish(request);
            log.info("SMS sent successfully with message ID: {}", response.messageId());
        } catch (Exception e) {
            log.error("Failed to send SMS: {}", e.getMessage(), e);
        }
    }
}