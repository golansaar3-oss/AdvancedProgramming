package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {

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
