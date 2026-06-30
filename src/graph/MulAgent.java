package graph;

import graph.TopicManagerSingleton.TopicManager;


/**
 * An agent that multiplies two numeric topic values and publishes the result.
 */
public class MulAgent implements Agent{
    private String name;

    private String[] subs;
    private String[] pubs;

    private double x;
    private double y;

    /**
     * Creates a multiplication agent that consumes two topics and publishes one result.
     *
     * @param subs the subscribed topics
     * @param pubs the published topics
     */
    public MulAgent(String[] subs,String[] pubs)
    {
        this.name = "MulAgent";
        this.pubs = pubs;
        this.subs = subs;

        reset();

        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(subs[1]).subscribe(this);

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
     * Resets the stored input values.
     */
    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
    }

    /**
     * Multiplies the two input values and republishes the result.
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

        // get the first value
        if(topic.equals(subs[0])){
            x = value;
        }
        // get the second value
        else if(topic.equals(subs[1])){
            y = value;
        }
        else{
            return;
        }

        double result = x * y;

        // publish the result
        TopicManagerSingleton.get().getTopic(pubs[0]).publish(new Message(result));
    }

    /**
     * Unsubscribes the agent from its topics.
     */
    @Override
    public void close() {
        TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
        manager.getTopic(subs[0]).unsubscribe(this);
        manager.getTopic(subs[1]).unsubscribe(this);

        manager.getTopic(pubs[0]).removePublisher(this);
    }
}