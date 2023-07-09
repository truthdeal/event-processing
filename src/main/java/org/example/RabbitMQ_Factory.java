package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQ_Factory {
    ConnectionFactory factory;
    public RabbitMQ_Factory(String url){
        factory = new ConnectionFactory();
        factory.setHost(url);
    }
    public void CreateQueue(String queueName){
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);
            String message = "Init queue";
            //channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void BasicPublish(String queueName, String message){
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Consumer GetConsumer(String queueName) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        return new DefaultConsumer(channel);
    }
    public void BasicConsume(Consumer consumer, String queueName){
        String message = "";
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String _message = new String(delivery.getBody(), "UTF-8");
                System.out.println("Message: " + _message);
                System.out.println("Consumer tag: " + consumerTag);
            };

            channel.basicConsume(queueName, true, consumer);
            //message = channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
