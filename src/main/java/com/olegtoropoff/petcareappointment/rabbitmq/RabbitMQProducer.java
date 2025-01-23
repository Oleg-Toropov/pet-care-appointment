package com.olegtoropoff.petcareappointment.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ message producer for publishing messages to a specified exchange.
 * <p>
 * This class provides functionality to send messages to a RabbitMQ exchange
 * using a configured routing key.
 */
@Service
public class RabbitMQProducer {

    /**
     * Template for interacting with RabbitMQ.
     */
    private final RabbitTemplate rabbitTemplate;


    /**
     * Constructs a new {@code RabbitMQProducer}.
     *
     * @param rabbitTemplate The {@link RabbitTemplate} instance used for message publishing.
     */
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends a message to the RabbitMQ exchange.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}
