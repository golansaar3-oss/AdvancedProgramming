package configs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graph.*;



/**
 * A graph node that represents either a topic or an agent and stores outgoing edges.
 */
public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    /**
     * Creates a node with the given name.
     *
     * @param name the node name
     */
    public Node(String name)
    {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    // Setters
    /**
     * Updates the node name.
     *
     * @param name the new node name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * Replaces the outgoing edge list.
     *
     * @param edges the new outgoing edges
     */
    public void setEdges(List<Node> edges)
    {
        this.edges = edges;
    }
    /**
     * Stores the message currently associated with this node.
     *
     * @param msg the message to store
     */
    public void setMessage(Message msg)
    {
        this.msg = msg;
    }

    //Getters
    /**
     * Returns the node name.
     *
     * @return the node name
     */
    public String getName()
    {
        return name;
    }
    /**
     * Returns the outgoing edges.
     *
     * @return the outgoing edges
     */
    public List<Node> getEdges()
    {
        return edges;
    }

    /**
     * Returns the node message, if one is set.
     *
     * @return the stored message
     */
    public Message getMessage()
    {
        return msg;
    }

    
    /**
     * Adds an outgoing edge to another node when the target is not null.
     *
     * @param n the target node
     */
    public void addEdge(Node n)
    {
        if(n != null)
    {
        this.edges.add(n);
    }
    }

    /**
     * Detects whether this node reaches itself through its outgoing edges.
     *
     * @return true if a cycle is reachable from this node, otherwise false
     */
    public boolean hasCycles()
    {
        Set<Node> visited = new HashSet<>();
        Set<Node> inPath = new HashSet<>();

        return hasCyclesHelper(this, visited, inPath);
    }

    /**
     * Recursive depth-first cycle check that tracks the current traversal path.
     *
     * @param node the node being visited
     * @param visited nodes already fully explored
     * @param inPath nodes currently on the recursion stack
     * @return true if a back edge is found, otherwise false
     */
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