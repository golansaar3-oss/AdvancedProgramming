package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent
{
    private Agent agent;
    private BlockingQueue<Message> blockingQueue;
    private Thread worker;
    private volatile boolean running = true;

    public ParallelAgent(Agent agent, int capacity)
    {
        this.agent = agent;
        blockingQueue = new ArrayBlockingQueue<>(capacity);
        worker = new Thread(() -> {
            while (running)
            {
                try {
                    Message msg = blockingQueue.take();
                    TopicMessage tm = (TopicMessage) msg;
                    agent.callback(tm.topic, tm.originalMessage);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.start();
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void reset() {
        agent.reset();
    }

    @Override
    public void callback(String topic, Message msg) {

        try {
            blockingQueue.put(new TopicMessage(topic, msg));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();}
    }

    @Override
    public void close() {
        running = false;
        worker.interrupt();
        agent.close();
    }

    private static class TopicMessage extends Message // extended class to contain both topic and msg
    {
        public final String topic;
        public final Message originalMessage;

        public TopicMessage(String topic, Message message)
        {
            super(message.asText);
            this.topic = topic;
            this.originalMessage = message;
        }
    }

}


