package com.olegtoropoff.petcareappointment.rabbitmq;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RabbitMQProducerTest {

    @InjectMocks
    private RabbitMQProducer rabbitMQProducer;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void sendMessage_WhenCalled_SendsMessageWithCorrectParameters() {
        String message = "Test message";

        rabbitMQProducer.sendMessage(message);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq(RabbitMQConfig.ROUTING_KEY),
                eq(message)
        );
    }
}
