package base.rabbitmq;

import com.rabbitmq.client.*;

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("consumeMessages: " + queueName);
        }
    }

}