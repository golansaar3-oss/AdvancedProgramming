package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe singleton providing access to the central topic manager.
 * All topics in the system are managed through this singleton instance.
 */
public class TopicManagerSingleton {

    /**
     * Utility class; do not instantiate.
     */
    private TopicManagerSingleton() {
    }

    /**
     * The actual topic manager that holds all topics. Uses lazy initialization via class loader.
     */
    public static class TopicManager{
        private static final TopicManager instance = new TopicManager();
        ConcurrentHashMap<String, Topic> map = new ConcurrentHashMap<>();

        private TopicManager(){}

        /**
         * Returns the topic with the given name, creating it if needed.
         *
         * @param name the topic name
         * @return the topic instance
         */
        public Topic getTopic(String name)
        {
            return map.computeIfAbsent(name, Topic::new);
        }

        /**
         * Returns all known topics.
         *
         * @return the topic collection
         */
        public Collection<Topic> getTopics()
        {
            return map.values();
        }
        
        /**
         * Removes all topics from the manager.
         */
        public void clear()
        {
           map.clear(); 
        }
    }

    /**
     * Returns the singleton topic manager.
     *
     * @return the shared topic manager instance
     */
    public static TopicManager get(){
        return TopicManager.instance;
    }
    
}
