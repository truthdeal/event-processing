package base.communication;

import base.backend.Node;

public class Publisher {


    public void Publish(int nodeId, String url){
        Node node = new Node(nodeId, url);

    }

}