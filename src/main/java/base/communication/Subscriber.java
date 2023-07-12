package base.communication;

import base.backend.Node;
import base.events.*;
import base.rabbitmq.RabbitMQConsumer;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;

import javax.swing.event.DocumentEvent;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subscriber {
    public static void main(String[] args) throws JsonProcessingException, InterruptedException, EPDeployException, EPCompileException {

        int nodeId = 0;
        Node node;

        String query = "";

        Set<Integer> targetNodeIds = new HashSet<Integer>();

        String url = "localhost";

        for(int i = 0; i<args.length-1; i++){
            switch(args[i]) {
                case "-n":
                    nodeId =Integer.valueOf(args[i+1]);
                case "-q":
                    query = args[i+1];
                case "-u":
                    url = args[i+1];
            }
        }

        System.out.println("Contacted Node: " + nodeId);
        System.out.println("Query: " + query);
        System.out.println("URL: " + url);

        node = new Node(nodeId, url);
        targetNodeIds = extractNodeIds(query);

        for(int id : targetNodeIds){
            try {
                node.Subscribe(id, query, "sub"+id);
            } catch (EPDeployException | EPCompileException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        List<BaseEvent> events = new ArrayList<BaseEvent>();

        BaseEvent eventA = new BaseEvent();
        eventA.EventType = "A";
        eventA.Message = "EventA message";

        BaseEvent eventB = new BaseEvent();
        eventB.EventType = "B";
        eventB.Message = "EventB message";

        BaseEvent eventC = new BaseEvent();
        eventC.EventType = "C";
        eventC.Message = "EventC message";

        BaseEvent eventD = new BaseEvent();
        eventD.EventType = "D";
        eventD.Message = "EventD message";

        BaseEvent eventE = new BaseEvent();
        eventE.EventType = "E";
        eventE.Message = "EventE message";

        BaseEvent eventF = new BaseEvent();
        eventF.EventType = "F";
        eventF.Message = "EventF message";

        CreateEventQueue(events, eventA, eventB, eventC, eventE, eventD, eventF);
        CreateEventQueue(events, eventA, eventA, eventA, eventA, eventA, eventA);


        //node.Subscribe(5, query, "exampleStatement");
            //node.consumeMessages("queue" + 5);
        for(int id : targetNodeIds) {
            for (BaseEvent event : events) {
                Node tempNode = new Node(id, url);
                event.NodeId = id;
                tempNode.Publish(event);
                Thread.sleep(100);
            }
        }
        for(int id : targetNodeIds) {
            //node.Unsubscribe("sub"+id);
        }
    }

    private static void CreateEventQueue(List<BaseEvent> events, BaseEvent eventA, BaseEvent eventB, BaseEvent eventC, BaseEvent eventD, BaseEvent eventE, BaseEvent eventF) {
        events.add(eventA);
        events.add(eventA);
        events.add(eventA);

        events.add(eventB);
        events.add(eventC);
        events.add(eventE);
        events.add(eventD);
        events.add(eventF);
    }

    public static Set<Integer> extractNodeIds(String query) {
        Set<Integer> nodeIds = new HashSet<Integer>();
        Pattern pattern = Pattern.compile("\\b\\d+\\b");
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            String match = matcher.group();
            int number = Integer.parseInt(match);
            nodeIds.add(number);
        }

        return nodeIds;
    }
}
