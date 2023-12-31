package base.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQ_Factory {
    public final ConnectionFactory factory;
    public RabbitMQ_Factory(String url, int port){
        factory = new ConnectionFactory();
        factory.setHost(url);
        //factory.setPort(port);
    }
    public void CreateQueue(String queueName){
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);
            //channel.basicPublish("", queueName, null, "init message".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void BasicPublish(String queueName, String message){
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Consumer GetConsumer(String queueName) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        return new DefaultConsumer(channel);
    }
}
