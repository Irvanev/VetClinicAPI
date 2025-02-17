package dev.clinic.emailservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.clinic.emailservice.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public EmailListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "queueEmail")
    public void receiveMessage(String message) {
        try {
            System.out.println("Получено сообщение: " + message);
            Map<String, String> msgMap = objectMapper.readValue(message, Map.class);
            String email = msgMap.get("email");
            String verificationCode = msgMap.get("verificationCode");
            emailService.sendCustomVerificationEmail(email, verificationCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
