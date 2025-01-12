package com.olegtoropoff.petcareappointment.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ setup.
 * <p>
 * This class defines the RabbitMQ components such as queue, exchange, and binding,
 * which are necessary for message publishing and consumption in the application.
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Name of the RabbitMQ queue.
     */
    public static final String QUEUE_NAME = "registration-queue";

    /**
     * Name of the RabbitMQ exchange.
     */
    public static final String EXCHANGE_NAME = "registration-exchange";

    /**
     * Routing key for binding the queue to the exchange.
     */
    public static final String ROUTING_KEY = "registration.key";

    /**
     * Creates a durable RabbitMQ queue.
     *
     * @return A {@link Queue} instance with the specified name.
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    /**
     * Creates a RabbitMQ topic exchange.
     *
     * @return A {@link TopicExchange} instance with the specified name.
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    /**
     * Binds the queue to the exchange using the specified routing key.
     *
     * @param queue    The {@link Queue} instance to be bound.
     * @param exchange The {@link TopicExchange} instance to bind to.
     * @return A {@link Binding} instance representing the queue-to-exchange binding.
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
