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

            String content = "<html>"
                    + "<body>"
                    + "<h1>Добро пожаловать!</h1>"
                    + "<p>Ваш код подтверждения: <strong>" + verificationCode + "</strong></p>"
                    + "<p>Спасибо за регистрацию в нашем сервисе.</p>"
                    + "</body>"
                    + "</html>";

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
