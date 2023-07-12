package base.rabbitmq;

import com.rabbitmq.client.*;

public class RabbitMQConsumer {
    private final ConnectionFactory factory;

    public DeliverCallback rabbitMQ_DeliverCallback;

    public RabbitMQConsumer() {
        factory = new ConnectionFactory();
        factory.setHost("localhost"); // Set the RabbitMQ server hostname
    }

    String Message = "";
    public void consumeMessages(String queueName, DeliverCallback deliverCallback) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}