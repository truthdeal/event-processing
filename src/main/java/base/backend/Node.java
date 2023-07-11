package base.backend;

import base.esper.EsperFactory;
import base.events.*;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPEventService;
import com.espertech.esper.runtime.client.UpdateListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import base.events.*;
import base.rabbitmq.RabbitMQConsumer;
import base.rabbitmq.RabbitMQ_Factory;
import java.util.*;

public class Node {
    private static final String BASE_QUEUE_NAME = "queue";
    public int Id;
    public String QueueName;

    RabbitMQ_Factory rabbitMQFactory;

    EsperFactory esperFactory;

    List<RabbitMQConsumer> rabbitMQConsumers = new ArrayList<RabbitMQConsumer>();
    Map<String, EPEventService> epEventServices = new HashMap<String, EPEventService>();

    public Node(int id, String url) {
        Id = id;
        QueueName = "queue" + Id;

        //rabbitmq setup
        rabbitMQFactory = new RabbitMQ_Factory(url);

        //esper setup
        esperFactory = new EsperFactory();
        esperFactory.AddEventType(EventA.class);
        esperFactory.AddEventType(EventB.class);
        esperFactory.AddEventType(EventC.class);
        esperFactory.AddEventType(EventD.class);
        esperFactory.AddEventType(EventE.class);
        esperFactory.AddEventType(EventF.class);
    }

    public boolean Subscribe(int nodeId, String filterQuery) {

        //pass filterQuery to Esper by creating an EPEventService that gets stored in the map
        try {
            final String finalFilterQuery = filterQuery;
            UpdateListener updateListener = (newData, oldData, _statement, _runtime) -> {
                int eventTypeId = (int) newData[0].get("EventType");
                String message = (String) newData[0].get("Message");

                System.out.println(String.format("%s::: Event Type:%s, Age: %d", finalFilterQuery, eventTypeId, message));
            };

            epEventServices.put(filterQuery, esperFactory.DeployingQuery(filterQuery, filterQuery, updateListener));
        } catch (EPCompileException | EPDeployException e) {
            return false;
        }

        DeliverCallback rabbitMQ_DeliverCallback = (consumerTag, delivery) -> {
            String body = new String(delivery.getBody(), "UTF-8");

            System.out.println("Received message: " + body);

            BaseEvent event = ParseMessageToBaseEvent(body);
            this.ForwardEventToEsper(event, filterQuery);
        };

        //enable subscription:
        RabbitMQConsumer consumer = new RabbitMQConsumer();
        consumer.consumeMessages(BASE_QUEUE_NAME + nodeId, rabbitMQ_DeliverCallback);


        return true;
    }

    public boolean Unsubscribe() {
        return true;
    }

    public void Publish(BaseEvent event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(event);
        rabbitMQFactory.BasicPublish(QueueName, message);
    }

    public void ForwardEventToEsper(BaseEvent event, String query) {
            //get epEventService
            epEventServices.get(query).sendEventBean(event, query);
    }

    public BaseEvent ParseMessageToBaseEvent(String message) throws JsonProcessingException {
        //parse message string into event class

        BaseEvent event;

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(message);
        int eventTypeId = jsonNode.get("EventType").asInt();
        switch(eventTypeId){
            case 0:
                event = objectMapper.readValue(message, EventA.class);
            case 1:
                event = objectMapper.readValue(message, EventB.class);
            case 2:
                event = objectMapper.readValue(message, EventC.class);
            case 3:
                event = objectMapper.readValue(message, EventD.class);
            case 4:
                event = objectMapper.readValue(message, EventE.class);
            case 5:
                event = objectMapper.readValue(message, EventF.class);
            default:
                event = objectMapper.readValue(message, EventA.class);
        }

        return event;



    }
}
