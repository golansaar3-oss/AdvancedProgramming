package configs;

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import graph.*;

public class GenericConfig implements Config{
    private String confFile;
    private List<ParallelAgent> agents;

    public GenericConfig()
    {
        agents = new ArrayList<>();
    }


    public void setConfFile(String confFile)
    {
        this.confFile = confFile;
    }


    @Override
    public void create() {
        try{
            List<String> lines = Files.readAllLines(Paths.get(confFile));

            if(lines.size() % 3 != 0)
            {
                return;
            }

            // scan the lines
            for(int i = 0; i <lines.size(); i+=3)
            {
                String className = lines.get(i).trim();

                String[] subs = lines.get(i+1).trim().split(",");
                String[] pubs = lines.get(i+2).trim().split(",");


                // get the type of agent
                Class<?> clazz = Class.forName(className);

                // get the constructor
                Constructor<?> constructor = clazz.getConstructor(String[].class, String[].class);

                // cast the agent
                Agent agent = (Agent) constructor.newInstance((Object) subs, (Object) pubs);

                ParallelAgent pa = new ParallelAgent(agent, 10);
                agents.add(pa);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            close();
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for(ParallelAgent pa: agents)
        {
            pa.close();
        }
        agents.clear();
    }


}
