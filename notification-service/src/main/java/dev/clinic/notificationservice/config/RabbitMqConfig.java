package dev.clinic.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String notificationQueue = "queueNotification";
    public static final String tokenQueue = "tokenNotification";

    static final String exchangeName = "exchange";

    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue, false);
    }

    @Bean
    public Queue tokenQueue() {
        return new Queue(tokenQueue, false);
    }

    @Bean
    public Exchange exchange() {
        return new TopicExchange(exchangeName, false, false);
    }

    @Bean
    public Binding notificationQueueBinding(Queue notificationQueue, Exchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with("notification.key").noargs();
    }

    @Bean
    public Binding tokenQueueBinding(Queue tokenQueue, Exchange exchange) {
        return BindingBuilder.bind(tokenQueue).to(exchange).with("token.key").noargs();
    }
}
