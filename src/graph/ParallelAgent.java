package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Wraps an agent to handle callbacks asynchronously in a separate worker thread.
 * Messages are queued in a blocking queue and processed sequentially by the worker thread,
 * allowing the main computation graph to continue without waiting for this agent's callback to complete.
 */
public class ParallelAgent implements Agent
{
    private Agent agent;
    private BlockingQueue<Message> blockingQueue;
    private Thread worker;
    private volatile boolean running = true;

    /**
     * Wraps an agent in a worker-thread queue.
     *
     * @param agent the wrapped agent
     * @param capacity the callback queue capacity
     */
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

    /**
     * Returns the wrapped agent name.
     *
     * @return the agent name
     */
    @Override
    public String getName() {
        return agent.getName();
    }

    /**
     * Resets the wrapped agent.
     */
    @Override
    public void reset() {
        agent.reset();
    }

    /**
     * Enqueues a callback for asynchronous processing.
     *
     * @param topic the topic name that triggered the callback
     * @param msg the published message
     */
    @Override
    public void callback(String topic, Message msg) {

        try {
            blockingQueue.put(new TopicMessage(topic, msg));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();}
    }

    /**
     * Stops the worker thread and closes the wrapped agent.
     */
    @Override
    public void close() {
        running = false;
        worker.interrupt();
        agent.close();
    }

    /**
     * Internal message wrapper that carries the topic name alongside the payload.
     */
    private static class TopicMessage extends Message // extended class to contain both topic and msg
    {
        public final String topic;
        public final Message originalMessage;

        /**
         * Creates a queued topic-message pair.
         *
         * @param topic the topic name
         * @param message the original message
         */
        public TopicMessage(String topic, Message message)
        {
            super(message.asText);
            this.topic = topic;
            this.originalMessage = message;
        }
    }

}


