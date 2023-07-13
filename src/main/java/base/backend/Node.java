package base.backend;

import base.esper.EsperFactory;
import base.events.*;
import base.queries.QueryContainer;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import base.rabbitmq.RabbitMQConsumer;
import base.rabbitmq.RabbitMQ_Factory;

import java.time.LocalDateTime;
import java.util.*;

public class Node implements Runnable{
    public static final String STATEMENT_BASE_NAME = "statement";
    private static final String BASE_QUEUE_NAME = "queue";
    public final String STATEMENT_NAME;
    public final int ID;
    public final RabbitMQ_Factory rabbitMQFactory;
    public String QueueName;

    public QueryContainer queryContainer;
    boolean running = true;


    EsperFactory esperFactory;


    List<RabbitMQConsumer> rabbitMQConsumers = new ArrayList<RabbitMQConsumer>();
    Map<String, EPEventService> epEventServices = new HashMap<String, EPEventService>();
    Map<String, Thread> subscriptions = new HashMap<String, Thread>();
    Map<String,Boolean> runningStates = new HashMap<String, Boolean>();

    public Node(int id, String url, int port, QueryContainer queryContainer) {
        ID = id;
        STATEMENT_NAME = "statement" + ID;
        QueueName = "queue" + ID;

        this.queryContainer = queryContainer;
        //rabbitmq setup
        rabbitMQFactory = new RabbitMQ_Factory(url, port);
        //esper setup
        esperFactory = new EsperFactory();
        esperFactory.AddEventType(BaseEvent.class);
    }


    @Override
    public void run() {

    }

    public void Subscribe(int nodeId, String statementName) throws EPDeployException, EPCompileException {

        Runnable subscription = () -> {
            //pass filterQuery to Esper by creating an EPEventService that gets stored in the map
            UpdateListener updateListener = (newData, oldData, _statement, _runtime) -> {

                String returnMessage = String.format("ESPER %s", statementName);

                try {
                    if(queryContainer.EventFilters.length > 0) {
                        for (String eventName : queryContainer.EventFilters) {
                            returnMessage += "EventType: " + newData[0].get(eventName + ".eventType").toString();
                            returnMessage += "NodeId: " + (int) newData[0].get(eventName + ".nodeId");
                            returnMessage += "TimeStamp: " + ((LocalDateTime) newData[0].get(eventName + ".timeStamp")).toString();

                            System.out.println(returnMessage);
                            returnMessage = "";
                        }
                    } else{
                        returnMessage += "EventType: " + newData[0].get("eventType").toString();
                        returnMessage += "Message: " + newData[0].get("message").toString();
                        returnMessage += "NodeId: " + (int) newData[0].get("nodeId");
                        returnMessage += "TimeStamp: " + ((LocalDateTime) newData[0].get("timeStamp")).toString();

                        System.out.println(returnMessage);
                    }
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            };

            try {
                epEventServices.put(statementName, esperFactory.DeployingQuery(statementName, queryContainer.Query, updateListener));
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
            RabbitMQConsumer consumer = new RabbitMQConsumer(rabbitMQFactory.factory);
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

        UpdateListener updateListener = (newData, oldData, _statement, _runtime) -> {
            try {
                if(queryContainer.EventFilters.length > 0) {
                    for (String eventName : queryContainer.EventFilters) {

                        PublishFilteredData(newData[0], eventName);
                    }
                } else{
                    PublishFilteredData(newData[0], "");
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        };

        try {
            epEventServices.put(STATEMENT_NAME, esperFactory.DeployingQuery(STATEMENT_NAME, queryContainer.Query, updateListener));
        } catch (EPCompileException|EPDeployException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
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

    private void PublishFilteredData(EventBean eventBean, String eventName){
        BaseEvent filteredEvent = new BaseEvent();
        if(eventName.length()>0){
            eventName+=".";
        }

        filteredEvent.EventType = eventBean.get(eventName + "eventType").toString();
        filteredEvent.NodeId = (int) eventBean.get(eventName + "nodeId");
        filteredEvent.Message = eventBean.get(eventName + "message").toString();
        filteredEvent.TimeStamp = (LocalDateTime) eventBean.get(eventName + "timeStamp");


        ObjectMapper objectMapper = new ObjectMapper();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(filteredEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        rabbitMQFactory.CreateQueue(QueueName);
        rabbitMQFactory.BasicPublish(QueueName, body);
    }
}
