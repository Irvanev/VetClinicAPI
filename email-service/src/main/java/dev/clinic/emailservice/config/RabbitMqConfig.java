package dev.clinic.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    static final String queueEmail = "queueEmail";

    static final String exchangeName = "exchange";

    @Bean
    public Queue emailQueue() {
        return new Queue(queueEmail, false);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(exchangeName, false, false);
    }

    @Bean
    public Binding emailQueueBinding(Queue emailQueue, Exchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with("email.key").noargs();
    }
}
