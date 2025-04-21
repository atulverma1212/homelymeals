package org.bits.pilani.homely.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/notification")
    @SendTo("/topic/admin-notifications")
    public String handleNotification(String message) {
        return message;
    }
}