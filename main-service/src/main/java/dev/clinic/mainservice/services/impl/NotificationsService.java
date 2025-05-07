package dev.clinic.mainservice.services.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationsService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDeviceTokenAndUserId(Long userId, String deviceToken) {
        String message = "{\"userId\":" + userId + ",\"deviceToken\":\"" + deviceToken + "\"}";
        try {
            rabbitTemplate.convertAndSend("exchange", "token.key", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationEventAsString(Long userId, String title, String body, Map<String, Object> payload) {
        String payloadJson = "";
        if (payload != null && !payload.isEmpty()) {
            payloadJson = payload.entrySet()
                    .stream()
                    .map(entry -> "\"" + entry.getKey() + "\":" + toJsonValue(entry.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        }

        String message = String.format(
                "{\"userId\":%d,\"title\":\"%s\",\"body\":\"%s\",\"payload\":%s}",
                userId, title, body, payloadJson.isEmpty() ? "null" : payloadJson
        );

        try {
            rabbitTemplate.convertAndSend("exchange", "notification.key", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toJsonValue(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else {
            return value.toString();
        }
    }
}
