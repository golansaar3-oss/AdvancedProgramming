package graph;

import graph.TopicManagerSingleton.TopicManager;

public class MulAgent implements Agent{
    private String name;

    private String[] subs;
    private String[] pubs;

    private double x;
    private double y;

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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
    }

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

    @Override
    public void close() {
        TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
        manager.getTopic(subs[0]).unsubscribe(this);
        manager.getTopic(subs[1]).unsubscribe(this);

        manager.getTopic(pubs[0]).removePublisher(this);
    }
}