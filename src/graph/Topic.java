package graph;

import java.util.ArrayList;

/**
 * Represents a communication channel in the publish-subscribe system.
 * Topics maintain lists of subscribing agents and notify them when messages are published.
 * Topics should be created through TopicManagerSingleton, not instantiated directly.
 */
public class Topic {
    /**
     * The topic name.
     */
    public final String name;
    private ArrayList<Agent> subs;
    private ArrayList<Agent> pubs;

    private Message lastMessage;

    /**
     * Creates a topic with the given name.
     *
     * @param name the topic name
     */
    Topic(String name){
        this.name=name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastMessage = null;
    }

    /**
     * Registers a subscriber for this topic.
     *
     * @param a the subscribing agent
     */
    public void subscribe(Agent a){
        subs.add(a);
    }
    /**
     * Removes a subscriber from this topic.
     *
     * @param a the unsubscribing agent
     */
    public void unsubscribe(Agent a){
        subs.remove(a);
    }

    /**
     * Stores the latest message and notifies all subscribers.
     *
     * @param m the message to publish
     */
    public void publish(Message m){
        this.lastMessage = m;
        for(Agent a : subs)
        {
            a.callback(name, m);
        }
    }

    /**
     * Registers a publisher for this topic.
     *
     * @param a the publishing agent
     */
    public void addPublisher(Agent a){
        pubs.add(a);
    }

    /**
     * Removes a publisher from this topic.
     *
     * @param a the removing agent
     */
    public void removePublisher(Agent a){
        pubs.remove(a);
    }

    /**
     * Returns the subscribers for this topic.
     *
     * @return the subscribers
     */
    public ArrayList<Agent> getSubs()
    {
        return subs;
    }

    /**
     * Returns the publishers for this topic.
     *
     * @return the publishers
     */
    public ArrayList<Agent> getPubs()
    {
        return pubs;
    }

    /**
     * Returns the last published message.
     *
     * @return the last message, or null if none has been published
     */
    public Message getLastMessage(){
        return lastMessage;
    }

}
