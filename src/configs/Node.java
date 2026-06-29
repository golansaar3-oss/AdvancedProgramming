package configs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graph.*;



public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    public Node(String name)
    {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    // Setters
    public void setName(String name)
    {
        this.name = name;
    }
    public void setEdges(List<Node> edges)
    {
        this.edges = edges;
    }
    public void setMessage(Message msg)
    {
        this.msg = msg;
    }

    //Getters
    public String getName()
    {
        return name;
    }
    public List<Node> getEdges()
    {
        return edges;
    }

    public Message getMessage()
    {
        return msg;
    }

    
    public void addEdge(Node n)
    {
        if(n != null)
    {
        this.edges.add(n);
    }
    }

    public boolean hasCycles()
    {
        Set<Node> visited = new HashSet<>();
        Set<Node> inPath = new HashSet<>();

        return hasCyclesHelper(this, visited, inPath);
    }

    private boolean hasCyclesHelper(Node node, Set<Node> visited, Set<Node> inPath)
    {
        if(inPath.contains(node))
            return true;

        if(visited.contains(node))
            return false;

        visited.add(node);
        inPath.add(node);

        for(Node neighbor : node.getEdges())
        {
            if(hasCyclesHelper(neighbor, visited, inPath))
                return true;
        }

        inPath.remove(node);
        return false;
    }

}