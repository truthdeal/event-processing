package base.backend;

import base.esper.EsperFactory;
import base.events.*;
import base.rabbitmq.RabbitMQConsumer;
import com.rabbitmq.client.DeliverCallback;
import base.events.*;
import base.rabbitmq.RabbitMQ_Factory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Backend {

    public static EsperFactory esperFactory;
    public static void main(String[] args) throws IOException, TimeoutException {

        String queueName = "EventAQueue";
        String message = "{\"EventType\": \"0\", \"Message\": Event A Message}";

        //rabbitmq setup
        RabbitMQ_Factory rabbitMQ_factory = new RabbitMQ_Factory("localhost");
        rabbitMQ_factory.CreateQueue(queueName);


        //esper setup
        esperFactory = new EsperFactory();
        esperFactory.AddEventType(EventA.class);
        esperFactory.AddEventType(EventB.class);
        esperFactory.AddEventType(EventC.class);
        esperFactory.AddEventType(EventD.class);
        esperFactory.AddEventType(EventE.class);
        esperFactory.AddEventType(EventF.class);


        //rabbitmq publish message
        rabbitMQ_factory.BasicPublish(queueName, message);


        //setup for a callback that gets called when the consumer consumes a message
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String body = new String(delivery.getBody(), "UTF-8");
            System.out.println("Received message: " + body);

            Node node = new Node(0, "localhost");


            BaseEvent event = node.ParseMessageToBaseEvent(body);
            // Pass the message content to another method for further processing
            node.ForwardEventToEsper(event, "select name, age from PersonEvent(age>=25)");
        };


        //rabbitmq consume message
        RabbitMQConsumer consumer = new RabbitMQConsumer();
        consumer.consumeMessages(queueName, deliverCallback);
    }



        // queries.txt dosyasının yolunu belirleyin
        //String queriesFilePath = "/Users/okanarslan/IdeaProjects/event-processing/src/main/java/queries.txt";

        /*try {
            // queries.txt dosyasını okuyun
            List<String> queries = Files.readAllLines(Paths.get(queriesFilePath));

            // Her bir CEP sorgusu için döngü
            for (String query : queries) {
                // Sorgunun eşleşip eşleşmediğini kontrol edin ve gerekli işlemleri gerçekleştirin
                if (isQueryMatched(query, message)) {
                    System.out.println("Matched query " + query + " for message: " + message);
                    // Yapılacak işlemleri burada gerçekleştirin
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    private static boolean isQueryMatched(String query, String message) {
        // Gelen mesajı ve CEP sorgusunu karşılaştırın ve eşleşip eşleşmediğini kontrol edin
        // Gerekli işlemleri gerçekleştirin
        return message.contains(query);
    }
}