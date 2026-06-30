package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe singleton providing access to the central topic manager.
 * Uses the class loader pattern to ensure lazy initialization and thread safety.
 * All topics in the system are managed through this singleton instance.
 */
public class TopicManagerSingleton {

    /**
     * The actual topic manager that holds all topics. Uses lazy initialization via class loader.
     */
    public static class TopicManager{
        private static final TopicManager instance = new TopicManager();
        ConcurrentHashMap<String, Topic> map = new ConcurrentHashMap<>();

        private TopicManager(){}

        public Topic getTopic(String name)
        {
            return map.computeIfAbsent(name, Topic::new);
        }

        public Collection<Topic> getTopics()
        {
            return map.values();
        }
        
        public void clear()
        {
           map.clear(); 
        }
    }

    public static TopicManager get(){
        return TopicManager.instance;
    }
    
}
