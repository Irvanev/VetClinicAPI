package dev.clinic.emailservice.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Test
    void sendCustomVerificationEmail_shouldSendEmail() throws Exception {
        String toEmail = "test@example.com";
        String code = "123456";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendCustomVerificationEmail(toEmail, code);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendCustomPasswordEmail_shouldSendEmail() throws Exception {
        String toEmail = "test@example.com";
        String password = "123456789";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendCustomPasswordEmail(toEmail, password);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendCustomVerificationEmail_shouldHandleExceptionGracefully() throws Exception {
        String toEmail = "test@example.com";
        String code = "123456";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Simulated failure")).when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.sendCustomVerificationEmail(toEmail, code));

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}