package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import configs.ConfigValidator;
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

    /**
     * Creates a configuration-loading servlet.
     */
    public ConfLoader() {
    }

    /**
     * Handles an uploaded configuration file, loads the graph, and returns its HTML view.
     *
     * @param ri the parsed request information
     * @param toClient the client output stream
     * @throws IOException if writing the response or saving the file fails
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {

        // get the body that came from the browser upload
        String body = new String(ri.getContent(), StandardCharsets.UTF_8);

        // extract only the content of the uploaded file
        String confText = getFileContent(body);

        if(confText == null || confText.equals(""))
        {
            sendResponse(toClient, getBadConfigHtml());
            return;
        }

        // validate before changing the current running configuration
        if(!ConfigValidator.isValid(confText))
        {
            sendResponse(toClient, getBadConfigHtml());
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

        // load the config
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

    /**
     * Extracts the uploaded file content from a multipart request body.
     *
     * @param body the raw multipart body
     * @return the extracted file content, or null if the body cannot be parsed
     */
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

    /**
     * Creates a friendly page for invalid configuration files.
     */
    private String getBadConfigHtml()
    {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<title>Bad Config</title>");

        html.append("<style>");
        html.append("body{margin:0;font-family:Arial,sans-serif;background:#f5f7fb;color:#222;}");
        html.append(".page{padding:18px;}");
        html.append(".card{background:white;border:1px solid #d0d7de;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.08);padding:22px;}");
        html.append("h2{margin-top:0;color:#b42318;}");
        html.append("p{color:#555;line-height:1.5;}");
        html.append(".format{background:#f8fbff;border:1px solid #d0d7de;border-radius:10px;padding:14px;margin-top:14px;}");
        html.append("pre{background:#1f2937;color:#f9fafb;padding:14px;border-radius:8px;overflow:auto;font-size:14px;}");
        html.append(".note{margin-top:14px;color:#666;font-size:14px;}");
        html.append("</style>");

        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"page\">");
        html.append("<div class=\"card\">");

        html.append("<h2>Bad Config File</h2>");
        html.append("<p>The uploaded configuration file could not be loaded.</p>");

        html.append("<div class=\"format\">");
        html.append("<p><b>Expected format:</b> each agent is written using 3 lines:</p>");
        html.append("<pre>");
        html.append("&lt;full agent class name&gt;\n");
        html.append("&lt;input topics separated by comma&gt;\n");
        html.append("&lt;output topics separated by comma&gt;");
        html.append("</pre>");

        html.append("<p><b>Example of working config:</b></p>");
        html.append("<pre>");
        html.append("configs.PlusAgent\n");
        html.append("A,B\n");
        html.append("C\n");
        html.append("configs.IncAgent\n");
        html.append("C\n");
        html.append("D");
        html.append("</pre>");
        html.append("</div>");

        html.append("<p class=\"note\">Fix the config file and upload it again using the Deploy button.</p>");

        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private void sendResponse(OutputStream toClient, String html) throws IOException
    {
        byte[] bodyBytes = html.getBytes(StandardCharsets.UTF_8);

        String header =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "\r\n";

        toClient.write(header.getBytes(StandardCharsets.UTF_8));
        toClient.write(bodyBytes);
        toClient.flush();
    }

    /**
     * Closes the current configuration, if one has been loaded.
     *
     * @throws IOException if closing the underlying config fails
     */
    @Override
    public void close() throws IOException {

        // close the config agents when the server closes
        if(currentConfig != null)
        {
            currentConfig.close();
        }
    }
}