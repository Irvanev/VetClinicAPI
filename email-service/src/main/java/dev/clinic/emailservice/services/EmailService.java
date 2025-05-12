package dev.clinic.emailservice.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Отправляет письмо с кодом подтверждения.
     *
     * @param toEmail          адрес получателя
     * @param verificationCode код подтверждения для отправки
     */
    public void sendCustomVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("irvvanevv@mail.ru");
            helper.setTo(toEmail);
            helper.setSubject("Подтверждение регистрации");

            String content =
                    "<html>" +
                    "<body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>" +
                    "<div style='background-color: #fff; padding: 30px; border-radius: 8px; max-width: 600px; margin: auto;'>" +
                    "<h2 style='color: #4CAF50;'>Подтверждение регистрации</h2>" +
                    "<p>Здравствуйте! Спасибо за регистрацию в нашем сервисе.</p>" +
                    "<p>Ваш код подтверждения:</p>" +
                    "<div style='font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0;'>" + verificationCode + "</div>" +
                    "<p>Если вы не запрашивали это письмо, просто проигнорируйте его.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCustomPasswordEmail(String toEmail, String password) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("irvvanevv@mail.ru");
            helper.setTo(toEmail);
            helper.setSubject("Подтверждение регистрации");

            String content = "<html>"
                    + "<body>"
                    + "<h1>Добро пожаловать!</h1>"
                    + "<p>Ваш временный пароль: <strong>" + password + "</strong></p>"
                    + "<p>Спасибо за регистрацию в нашем сервисе. Приятной работы!</p>"
                    + "</body>"
                    + "</html>" ;

            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
