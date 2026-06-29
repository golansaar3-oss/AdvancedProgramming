package graph;

import graph.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent{

    private String name;

    private String[] subs;
    private String[] pubs;

    public IncAgent(String[] subs, String[] pubs)
    {
        this.subs = subs;
        this.pubs = pubs;

        this.name = "IncAgent";

        TopicManager tm = TopicManagerSingleton.get();

        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(pubs[0]).addPublisher(this);
        
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void reset() {
        return;
    }

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

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
        manager.getTopic(subs[0]).unsubscribe(this);
        manager.getTopic(pubs[0]).removePublisher(this);
    }

    
}
