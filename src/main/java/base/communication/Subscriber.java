package base.communication;

import base.backend.Node;
import base.events.*;
import base.queries.AllQueries;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subscriber {
    public static void main(String[] args) throws JsonProcessingException, InterruptedException, EPDeployException, EPCompileException {

        int nodeId = 0;
        Node node;

        String query = "";

        Set<Integer> subscribedNodeIds = new HashSet<Integer>();

        String url = "localhost";

        for(int i = 0; i<args.length-1; i++){
            switch(args[i]) {
                case "-n":
                    nodeId =Integer.valueOf(args[i+1]);
                case "-q":
                    query = args[i+1];
                case "-u":
                    url = args[i+1];
                case "-s":
                    subscribedNodeIds = extractNodeIds(args[i+1]);
            }
        }

        System.out.println("Contacted Node: " + nodeId);
        System.out.println("Query: " + query);
        System.out.println("URL: " + url);

        Node[] nodes = InitNodes();
        Node contactedNode = nodes[nodeId-1];

        for(int id : subscribedNodeIds){
            try {
                contactedNode.Subscribe(id, Node.STATEMENT_BASE_NAME+id);
            } catch (EPDeployException | EPCompileException e) {
                System.out.println(e.getMessage());
            }
        }

        List<BaseEvent> events = new ArrayList<BaseEvent>();

        BaseEvent eventA = new BaseEvent();
        eventA.EventType = "A";
        eventA.Message = "1";

        BaseEvent eventB = new BaseEvent();
        eventB.EventType = "B";
        eventB.Message = "B";

        BaseEvent eventC = new BaseEvent();
        eventC.EventType = "C";
        eventC.Message = "C";

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


        List<BaseEvent> allEvents = new ArrayList<BaseEvent>();
        CreateEventQueue(allEvents, eventA, eventB, eventC, eventD, eventE,eventF);

        while(true) {
            int randPublishNodeId =  new Random().nextInt(8);
            int randTargetNodeId = new Random().nextInt(8) + 1;
            int randEvent = new Random().nextInt(5) ;

            Node publishingNode = nodes[randPublishNodeId];
            BaseEvent pubEvent = allEvents.get(randEvent);
            pubEvent.NodeId = randTargetNodeId;
            pubEvent.Message = "" + randPublishNodeId;
            publishingNode.Publish(pubEvent);

            Thread.sleep(500%17);
        }
    }

    private static void CreateEventQueue(List<BaseEvent> events, BaseEvent eventA, BaseEvent eventB, BaseEvent eventC, BaseEvent eventD, BaseEvent eventE, BaseEvent eventF) {
        events.add(eventA);
        events.add(eventB);
        events.add(eventC);
        events.add(eventE);
        events.add(eventD);
        events.add(eventF);
    }

    private static Node[] InitNodes(){
        Node[] nodes = new Node[9];
        for(int i = 0; i< 9; i++){
            nodes[i] = new Node(i+1,"localhost", 5672+i,  AllQueries.QUERY_CONTAINERS[i]);
        }

        return nodes;
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
