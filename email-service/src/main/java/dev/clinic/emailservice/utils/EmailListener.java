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

    @RabbitListener(queues = "queueVerificationCodeEmail")
    public void receiveVerificationCodeMessage(String message) {
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

    @RabbitListener(queues = "queuePasswordEmail")
    public void receivePasswordMessage(String message) {
        try {
            System.out.println("Получено сообщение: " + message);
            Map<String, String> msgMap = objectMapper.readValue(message, Map.class);
            String email = msgMap.get("email");
            String password = msgMap.get("password");
            emailService.sendCustomPasswordEmail(email, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
