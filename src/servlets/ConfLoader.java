package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import configs.GenericConfig;
import configs.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;

/**
 * Loads computational graph configurations from uploaded files, creates topics and agents, and returns an HTML visualization.
 * Handles multipart form uploads, parses configuration files, and generates graph displays.
 */
public class ConfLoader implements Servlet {

    private GenericConfig currentConfig;

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {

        // get the body that came from the browser upload
        String body = new String(ri.getContent(), StandardCharsets.UTF_8);

        // extract only the content of the uploaded file
        String confText = getFileContent(body);

        if(confText == null || confText.equals(""))
        {
            String html = "<html><body><h2>Could not read config file</h2></body></html>";
            sendResponse(toClient, html);
            return;
        }

        // close old config if there is one
        if(currentConfig != null)
        {
            currentConfig.close();
        }

        // clear old topics before loading the new config
        TopicManagerSingleton.get().clear();

        // save the uploaded config to the server side
        Files.createDirectories(Paths.get("config_files"));
        Files.write(Paths.get("config_files/uploaded.conf"), confText.getBytes(StandardCharsets.UTF_8));

        // load the config using the class from assignment 4
        currentConfig = new GenericConfig();
        currentConfig.setConfFile("config_files/uploaded.conf");
        currentConfig.create();

        // create the graph from the topics
        Graph graph = new Graph();
        graph.createFromTopics();

        // create html for the graph
        List<String> graphHtml = HtmlGraphWriter.getGraphHTML(graph);

        StringBuilder html = new StringBuilder();

        for(String line : graphHtml)
        {
            html.append(line);
            html.append("\n");
        }

        sendResponse(toClient, html.toString());
    }

    private String getFileContent(String body)
    {
        // multipart body contains headers and then the file content.
        // the file content starts after an empty line.
        int start = body.indexOf("\r\n\r\n");

        if(start != -1)
        {
            start += 4;
        }
        else
        {
            start = body.indexOf("\n\n");

            if(start == -1)
            {
                return null;
            }

            start += 2;
        }

        String fileContent = body.substring(start);

        // remove the ending boundary from multipart body
        int boundaryIndex = fileContent.lastIndexOf("\r\n--");

        if(boundaryIndex == -1)
        {
            boundaryIndex = fileContent.lastIndexOf("\n--");
        }

        if(boundaryIndex != -1)
        {
            fileContent = fileContent.substring(0, boundaryIndex);
        }

        // clean final new lines
        while(fileContent.endsWith("\r\n") || fileContent.endsWith("\n"))
        {
            if(fileContent.endsWith("\r\n"))
            {
                fileContent = fileContent.substring(0, fileContent.length() - 2);
            }
            else if(fileContent.endsWith("\n"))
            {
                fileContent = fileContent.substring(0, fileContent.length() - 1);
            }
        }

        return fileContent;
    }

    private void sendResponse(OutputStream toClient, String html) throws IOException
    {
        byte[] bodyBytes = html.getBytes(StandardCharsets.UTF_8);

        String header =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "\r\n";

        toClient.write(header.getBytes(StandardCharsets.UTF_8));
        toClient.write(bodyBytes);
        toClient.flush();
    }

    @Override
    public void close() throws IOException {

        // close the config agents when the server closes
        if(currentConfig != null)
        {
            currentConfig.close();
        }
    }
}