package configs;

import java.util.ArrayList;
import java.util.HashMap;

import graph.*;

/**
 * Represents a computational graph as a collection of nodes and edges.
 * Creates nodes from topics and agents in the topic manager, with edges representing data flow.
 * Can detect cycles in the graph.
 */
public class Graph extends ArrayList<Node>{
    
    public boolean hasCycles() {
        for(Node node : this)
        {
            if(node.hasCycles())
                return true;
        }
        return false;
    }
    public void createFromTopics()
{
    // Remove previous graph content
    this.clear();

    // Prevent duplicate graph nodes
    HashMap<String, Node> nodesMap = new HashMap<>();

    TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();

    // Iterate over all existing topics
    for(Topic topic : manager.getTopics())
    {
        // Create/get Topic graph node
        String topicNodeName = "T" + topic.name;

        nodesMap.putIfAbsent(topicNodeName, new Node(topicNodeName));

        Node topicNode = nodesMap.get(topicNodeName);

        // Topic -> Agent edges (subscribers)
        for(Agent agent : topic.getSubs())
        {
            String agentNodeName = "A" + agent.getName();

            nodesMap.putIfAbsent(agentNodeName, new Node(agentNodeName));

            Node agentNode = nodesMap.get(agentNodeName);

            topicNode.addEdge(agentNode);
        }

        // Agent -> Topic edges (publishers)
        for(Agent agent : topic.getPubs())
        {
            String agentNodeName = "A" + agent.getName();

            nodesMap.putIfAbsent(agentNodeName, new Node(agentNodeName));

            Node agentNode = nodesMap.get(agentNodeName);

            agentNode.addEdge(topicNode);
        }
    }

    // Add all created nodes into the graph
    this.addAll(nodesMap.values());
}

    
}
