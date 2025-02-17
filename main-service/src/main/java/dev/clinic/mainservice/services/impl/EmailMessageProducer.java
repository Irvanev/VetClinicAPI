package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.configs.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    static final String queueEmail = "queueEmail";

    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            Map<String, String> message = new HashMap<>();
            message.put("email", email);
            message.put("verificationCode", verificationCode);
            // Дополнительные параметры (тема, имя пользователя и т.д.) можно добавить здесь
            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = mapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend("exchange", "email.key", jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
