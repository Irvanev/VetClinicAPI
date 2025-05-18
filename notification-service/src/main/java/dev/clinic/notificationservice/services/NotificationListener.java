package dev.clinic.notificationservice.services;

import dev.clinic.notificationservice.config.RabbitMqConfig;
import dev.clinic.notificationservice.dtos.NotificationEvent;
import dev.clinic.notificationservice.models.entities.DeviceToken;
import dev.clinic.notificationservice.repositories.DeviceTokenRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class NotificationListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final DeviceTokenRepository tokenRepository;
    private final FcmService fcmService;

    @Autowired
    public NotificationListener(
            SimpMessagingTemplate messagingTemplate,
            DeviceTokenRepository tokenRepository,
            FcmService fcmService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.tokenRepository = tokenRepository;
        this.fcmService = fcmService;
    }

    @RabbitListener(queues = RabbitMqConfig.notificationQueue)
    public void onNotification(NotificationEvent evt) {
        messagingTemplate.convertAndSendToUser(
                evt.getUserId().toString(),
                "/queue/notifications",
                evt
        );
        var tokens = tokenRepository.findAllByUserId(evt.getUserId())
                .stream().map(DeviceToken::getToken).collect(Collectors.toList());
        fcmService.sendPush(evt, tokens);
    }
}
