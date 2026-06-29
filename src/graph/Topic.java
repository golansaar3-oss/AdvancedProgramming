package graph;

import java.util.ArrayList;

/**
 * Represents a communication channel in the publish-subscribe system.
 * Topics maintain lists of subscribing agents and notify them when messages are published.
 * Topics should be created through TopicManagerSingleton, not instantiated directly.
 */
public class Topic {
    public final String name;
    private ArrayList<Agent> subs;
    private ArrayList<Agent> pubs;

    private Message lastMessage;

    Topic(String name){
        this.name=name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastMessage = null;
    }

    public void subscribe(Agent a){
        subs.add(a);
    }
    public void unsubscribe(Agent a){
        subs.remove(a);
    }

    public void publish(Message m){
        this.lastMessage = m;
        for(Agent a : subs)
        {
            a.callback(name, m);
        }
    }

    public void addPublisher(Agent a){
        pubs.add(a);
    }

    public void removePublisher(Agent a){
        pubs.remove(a);
    }

    public ArrayList<Agent> getSubs()
    {
        return subs;
    }

    public ArrayList<Agent> getPubs()
    {
        return pubs;
    }

    public Message getLastMessage(){
        return lastMessage;
    }

}
