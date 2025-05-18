package dev.clinic.notificationservice.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import dev.clinic.notificationservice.dtos.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FcmService {
    private final FirebaseMessaging messaging;

    @Autowired
    public FcmService(FirebaseMessaging messaging) {
        this.messaging = messaging;
    }

    public void sendPush(NotificationEvent evt, List<String> deviceTokens) {

        Map<String, String> data = evt.getPayload() != null
                ? evt.getPayload().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() == null ? "" : e.getValue().toString()
                ))
                : Collections.emptyMap();

        deviceTokens.forEach(deviceToken -> {
            Message msg = Message.builder()
                    .setToken(deviceToken)
                    .putData("title", evt.getTitle())
                    .putData("body", evt.getBody())
                    .putAllData(data)
                    .build();
            messaging.sendAsync(msg);
        });
    }
}

