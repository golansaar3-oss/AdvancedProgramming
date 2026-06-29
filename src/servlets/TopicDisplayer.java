package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;

/**
 * Web interface for the publish-subscribe system.
 * Accepts topic and message parameters, publishes the message, and returns an HTML table of all topic values.
 */
public class TopicDisplayer implements Servlet {

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {

        // get the parameters from the request
        String topic = ri.getParameters().get("topic");
        String message = ri.getParameters().get("message");

        // make sure both parameters exist
        if (topic == null || message == null) {

            String body = "<html><body><h2>Missing parameters</h2></body></html>";

            String response =
                    "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "\r\n" +
                    body;

            toClient.write(response.getBytes(StandardCharsets.UTF_8));
            return;
        }

        // publish the new message
        TopicManagerSingleton.get().getTopic(topic).publish(new Message(message));

        // create the html table
        StringBuilder html = new StringBuilder();

        html.append("<html>");
        html.append("<body>");
        html.append("<h2>Topics</h2>");

        html.append("<table border=\"1\">");
        html.append("<tr>");
        html.append("<th>Topic</th>");
        html.append("<th>Last Value</th>");
        html.append("</tr>");

        // go over all existing topics
        for (Topic t : TopicManagerSingleton.get().getTopics()) {

            html.append("<tr>");

            html.append("<td>");
            html.append(t.name);
            html.append("</td>");

            html.append("<td>");

            if (t.getLastMessage() != null) {
                html.append(t.getLastMessage().asText);
            }

            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</body>");
        html.append("</html>");

        // create the http response
        byte[] bodyBytes = html.toString().getBytes(StandardCharsets.UTF_8);

        String header =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "\r\n";

        // send the response
        toClient.write(header.getBytes(StandardCharsets.UTF_8));
        toClient.write(bodyBytes);
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // nothing to close
    }
}