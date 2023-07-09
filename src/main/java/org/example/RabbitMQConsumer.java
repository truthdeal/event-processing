package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RabbitMQConsumer {
    private final ConnectionFactory factory;

    public RabbitMQConsumer() {
        factory = new ConnectionFactory();
        factory.setHost("localhost"); // Set the RabbitMQ server hostname
    }

    String Message = "";
    public void consumeMessages(String queueName, DeliverCallback deliverCallback) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processMessage(String message) {
        // Use the message content in other parts of your code
        System.out.println("Processing message: " + message);
        Message = message;
        // Add your custom logic here
    }

}