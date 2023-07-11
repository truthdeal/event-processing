package base.communication;

import base.backend.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Subscriber {
    public static void main(String[] args) {

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
            node.Subscribe(id, query);
        }

        //keep program running
        boolean exit = false;
        while(!exit){
            Scanner in = new Scanner(System.in);
            String s = in.nextLine();
            if(s.toLowerCase() == "stop") {
                exit = true;
            }
        }

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
