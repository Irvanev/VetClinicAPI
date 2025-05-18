package dev.clinic.notificationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.clinic.notificationservice.config.RabbitMqConfig;
import dev.clinic.notificationservice.dtos.DeviceTokenEvent;
import dev.clinic.notificationservice.models.entities.DeviceToken;
import dev.clinic.notificationservice.repositories.DeviceTokenRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenListener {
    private final DeviceTokenRepository tokenRepository;

    @Autowired
    public DeviceTokenListener(DeviceTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @RabbitListener(queues = RabbitMqConfig.tokenQueue)
    public void handle(String message) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            DeviceTokenEvent evt = objectMapper.readValue(message, DeviceTokenEvent.class);

            System.out.println("Ооо, получено");

            if (!tokenRepository.existsByUserIdAndToken(evt.getUserId(), evt.getDeviceToken())) {
                DeviceToken dt = new DeviceToken();
                dt.setUserId(evt.getUserId());
                dt.setToken(evt.getDeviceToken());
                tokenRepository.save(dt);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при разборе сообщения из очереди: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
