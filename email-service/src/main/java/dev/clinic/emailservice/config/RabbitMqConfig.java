package dev.clinic.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    static final String emailVerificationCodeQueue = "queueVerificationCodeEmail";
    static final String emailPasswordQueue = "queuePasswordEmail";

    static final String exchangeName = "exchange";

    @Bean
    public Queue emailVerificationCodeQueue() {
        return new Queue(emailVerificationCodeQueue, false);
    }

    @Bean
    public Queue emailPasswordQueue() {
        return new Queue(emailPasswordQueue, false);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(exchangeName, false, false);
    }

    @Bean
    public Binding emailVerificationCodeQueueBinding(Queue emailVerificationCodeQueue, Exchange exchange) {
        return BindingBuilder.bind(emailVerificationCodeQueue).to(exchange).with("emailVerificationCode.key").noargs();
    }

    @Bean
    public Binding emailPasswordQueueBinding(Queue emailPasswordQueue, Exchange exchange) {
        return BindingBuilder.bind(emailPasswordQueue).to(exchange).with("emailPassword.key").noargs();
    }
}
