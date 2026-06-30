package graph;

/**
 * Represents a participant in the publish-subscribe system that can listen to and publish messages.
 * Agents subscribe to topics to receive callbacks when messages arrive, and can publish their own messages to other topics.
 */
public interface Agent {
    /**
     * Returns the agent name.
     *
     * @return the agent name
     */
    String getName();
    /**
     * Resets the agent state between runs.
     */
    void reset();
    /**
     * Receives a topic callback with the published message.
     *
     * @param topic the topic name that triggered the callback
     * @param msg the published message
     */
    void callback(String topic, Message msg);
    /**
     * Releases resources and unsubscribes the agent.
     */
    void close();
}
