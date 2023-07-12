package base.backend;

import base.esper.EsperFactory;
import base.events.*;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.util.DateTime;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DeliverCallback;
import base.events.*;
import base.rabbitmq.RabbitMQConsumer;
import base.rabbitmq.RabbitMQ_Factory;

import java.time.LocalDateTime;
import java.util.*;

public class Node implements Runnable{
    private static final String BASE_QUEUE_NAME = "queue";
    public int Id;
    public String QueueName;

    public String Query;
    boolean running = true;

    RabbitMQ_Factory rabbitMQFactory;

    EsperFactory esperFactory;


    List<RabbitMQConsumer> rabbitMQConsumers = new ArrayList<RabbitMQConsumer>();
    Map<String, EPEventService> epEventServices = new HashMap<String, EPEventService>();
    Map<String, Thread> subscriptions = new HashMap<String, Thread>();
    Map<String,Boolean> runningStates = new HashMap<String, Boolean>();

    public Node(int id, String url) {
        Id = id;
        QueueName = "queue" + Id;

        //rabbitmq setup
        rabbitMQFactory = new RabbitMQ_Factory(url);

        //esper setup
        esperFactory = new EsperFactory();
        esperFactory.AddEventType(BaseEvent.class);
    }


    @Override
    public void run() {

    }

    public void Subscribe(int nodeId, String filterQuery, String statementName) throws EPDeployException, EPCompileException {

        Runnable subscription = () -> {
            //pass filterQuery to Esper by creating an EPEventService that gets stored in the map

            final String finalFilterQuery = filterQuery;
            UpdateListener updateListener = (newData, oldData, _statement, _runtime) -> {

                try {
                    String eventTypeId = newData[0].get("eventF.eventType").toString();
                    String message = newData[0].get("eventF.message").toString();
                    int _nodeId = (int) newData[0].get("eventF.nodeId");
                    LocalDateTime timeStamp = (LocalDateTime) newData[0].get("eventF.timeStamp");

                    System.out.println(String.format("ESPER %s::: Event Type:%s, Message: %s", statementName, eventTypeId, message));
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            };

            try {
                epEventServices.put(statementName, esperFactory.DeployingQuery(statementName, filterQuery, updateListener));
            } catch (EPCompileException|EPDeployException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }

            //rabbitmq deliver callback
            DeliverCallback rabbitMQ_DeliverCallback = (consumerTag, delivery) -> {
                String body = new String(delivery.getBody(), "UTF-8");

                System.out.println("RabbitMQ: " + body);

                BaseEvent event = ParseMessageToBaseEvent(body);
                event.TimeStamp = LocalDateTime.now();

                this.ForwardEventToEsper(event, statementName);
            };

            //enable subscription:
            RabbitMQConsumer consumer = new RabbitMQConsumer();
            consumer.consumeMessages(BASE_QUEUE_NAME + nodeId, rabbitMQ_DeliverCallback);
            while (runningStates.get(statementName)) {
            }
            System.out.println(String.format("Subscription %s was cancelled", statementName));
        };
        Thread thread = new Thread(subscription);
        runningStates.put(statementName, true);
        thread.start();
        subscriptions.put(statementName, thread);
    }

    public void Unsubscribe(String statementName) {
        runningStates.put(statementName, false);
    }

    public void Publish(BaseEvent event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(event);
        rabbitMQFactory.CreateQueue(QueueName);
        rabbitMQFactory.BasicPublish(QueueName, message);
    }

    public void ForwardEventToEsper(BaseEvent event, String statementName) {
        //get epEventService
        EPStatement statement = esperFactory.statements.get(statementName);

        EPEventService eventService =epEventServices.get(statementName);
        eventService.sendEventBean(event, "BaseEvent");
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
