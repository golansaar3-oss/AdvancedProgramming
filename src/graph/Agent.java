package graph;

/**
 * Represents a participant in the publish-subscribe system that can listen to and publish messages.
 * Agents subscribe to topics to receive callbacks when messages arrive, and can publish their own messages to other topics.
 */
public interface Agent {
    String getName();
    void reset();
    void callback(String topic, Message msg);
    void close();
}
