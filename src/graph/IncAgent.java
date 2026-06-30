package graph;

import graph.TopicManagerSingleton.TopicManager;


/**
 * An agent that increments a numeric topic value by one.
 */
public class IncAgent implements Agent{

    private String name;

    private String[] subs;
    private String[] pubs;

    /**
     * Creates an increment agent that subscribes to one topic and publishes to one topic.
     *
     * @param subs the subscribed topics
     * @param pubs the published topics
     */
    public IncAgent(String[] subs, String[] pubs)
    {
        this.subs = subs;
        this.pubs = pubs;

        this.name = "IncAgent";

        TopicManager tm = TopicManagerSingleton.get();

        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(pubs[0]).addPublisher(this);
        
    }
    /**
     * Returns the agent name.
     *
     * @return the agent name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Resets the agent state.
     */
    @Override
    public void reset() {
        return;
    }

    /**
     * Increments numeric messages from the subscribed topic and republishes them.
     *
     * @param topic the topic name that triggered the callback
     * @param msg the published message
     */
    @Override
    public void callback(String topic, Message msg) {
        if(msg == null)
            return;

        double value = msg.asDouble;
        
        if(Double.isNaN(value))
            return;

        if(!topic.equals(subs[0]))
            return;
            
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(value + 1));
        
    }

    /**
     * Unsubscribes the agent from its topics.
     */
    @Override
    public void close() {
        TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
        manager.getTopic(subs[0]).unsubscribe(this);
        manager.getTopic(pubs[0]).removePublisher(this);
    }

    
}
