package org.example;

import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeoutException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static EsperFactory esperFactory;
    public static void main(String[] args) throws IOException, TimeoutException {

        String queueName = "EventAQueue";
        String message = "{\"EventType\": \"0\", \"Message\": Event A Message}";

        //rabbitmq setup
        RabbitMQ_Factory rabbitMQ_factory = new RabbitMQ_Factory("localhost");
        rabbitMQ_factory.CreateQueue(queueName);


        //esper setup
        esperFactory = new EsperFactory();
        esperFactory.Init();
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

            // Pass the message content to another method for further processing
            evaluateQuery(body);
        };


        //rabbitmq consume message
        RabbitMQConsumer consumer = new RabbitMQConsumer();
        consumer.consumeMessages(queueName, deliverCallback);
    }


    private static void evaluateQuery(String message) {

        BaseEvent event;
        try {
            //parse message string into event class
            ObjectMapper objectMapper = new ObjectMapper();

            event = objectMapper.readValue(message, BaseEvent.class);

            //configure filtering
            //ex:
            //filter PersonEvents with age >=25: "select name, age from PersonEvent(age>=25)"

            EPEventService eventService = esperFactory.DeployingQuery("PersonStatement", "select name, age from PersonEvent(age>=25)");

            System.out.println("sending event bean:");
            eventService.sendEventBean(event, "PersonEvent");
        } catch (JsonProcessingException | EPCompileException | EPDeployException e) {
            System.out.println(e.getMessage());
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

    }

    private static boolean isQueryMatched(String query, String message) {
        // Gelen mesajı ve CEP sorgusunu karşılaştırın ve eşleşip eşleşmediğini kontrol edin
        // Gerekli işlemleri gerçekleştirin
        return message.contains(query);
    }
}