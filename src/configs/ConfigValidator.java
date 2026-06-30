package configs;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import graph.Agent;

/**
 * Validates configuration text before it is loaded by GenericConfig.
 * This class checks only the structure and reflection requirements of the config file.
 */
public class ConfigValidator {

    /**
     * Utility class; do not instantiate.
     */
    private ConfigValidator() {
    }

    /**
     * Checks if the given configuration text can be loaded as a valid config.
     *
     * @param confText the uploaded configuration text
     * @return true if the config is valid, false otherwise
     */
    public static boolean isValid(String confText) {

        if(confText == null || confText.trim().equals(""))
        {
            return false;
        }

        List<String> lines = getCleanLines(confText);

        if(lines.size() == 0 || lines.size() % 3 != 0)
        {
            return false;
        }

        for(int i = 0; i < lines.size(); i += 3)
        {
            String className = lines.get(i);
            String subsLine = lines.get(i + 1);
            String pubsLine = lines.get(i + 2);

            if(className.equals("") || subsLine.equals("") || pubsLine.equals(""))
            {
                return false;
            }

            if(!isValidAgentClass(className))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Removes empty lines and trims each line.
     * This makes the validator a little more friendly for uploaded files.
     */
    private static List<String> getCleanLines(String confText) {

        List<String> cleanLines = new ArrayList<>();
        String[] lines = confText.split("\\r?\\n");

        for(String line : lines)
        {
            String cleanLine = line.trim();

            if(!cleanLine.equals(""))
            {
                cleanLines.add(cleanLine);
            }
        }

        return cleanLines;
    }

    /**
     * Checks that the class exists, implements Agent, and has the required constructor.
     */
    private static boolean isValidAgentClass(String className) {

        try
        {
            Class<?> clazz = Class.forName(className);

            if(!Agent.class.isAssignableFrom(clazz))
            {
                return false;
            }

            Constructor<?> constructor = clazz.getConstructor(String[].class, String[].class);

            return constructor != null;
        }
        catch(Exception ex)
        {
            return false;
        }
    }
}