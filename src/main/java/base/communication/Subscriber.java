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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Subscriber {
    public static void main(String[] args) throws JsonProcessingException, InterruptedException, EPDeployException, EPCompileException {

        int nodeId = 0;
        Node node;

        String query = "";

        List<Integer> targetNodeIds = new ArrayList<Integer>();

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
                node.Subscribe(id, query, "exampleStatement");
            } catch (EPDeployException | EPCompileException e) {
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

        events.add(eventA);
        events.add(eventA);
        events.add(eventA);

        events.add(eventE);
        events.add(eventC);
        events.add(eventD);

        events.add(eventA);
        events.add(eventA);
        events.add(eventA);
        events.add(eventA);
        events.add(eventA);
        events.add(eventA);
        events.add(eventA);
        events.add(eventA);


        node.Subscribe(5, query, "exampleStatement");
            //node.consumeMessages("queue" + 5);
        for(BaseEvent event : events){
            Node tempNode = new Node(5, url);
            tempNode.Publish(event);
            Thread.sleep(100);
        }

        node.Unsubscribe("exampleStatement");
    }

    public static List<Integer> extractNodeIds(String query) {
        List<Integer> nodeIds = new ArrayList<>();
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
