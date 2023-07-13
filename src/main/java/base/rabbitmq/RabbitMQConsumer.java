package base.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer {
    private final ConnectionFactory factory;

    public DeliverCallback rabbitMQ_DeliverCallback;

    public RabbitMQConsumer(ConnectionFactory _factory) {
        factory = _factory;
    }

    String Message = "";
    public void consumeMessages(String queueName, DeliverCallback deliverCallback) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            System.out.println(e);
            System.out.println("consumeMessages: " + queueName);
        }
    }

}