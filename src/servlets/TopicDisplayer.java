package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;

/**
 * Web interface for the publish-subscribe system.
 * Accepts topic and message parameters, publishes the message, and returns an HTML table of all topic values.
 */
public class TopicDisplayer implements Servlet {

    /**
     * Creates the topic display servlet.
     */
    public TopicDisplayer() {
    }

    /**
     * Publishes the requested message and renders the current topic values as HTML.
     *
     * @param ri the parsed request information
     * @param toClient the client output stream
     * @throws IOException if writing the response fails
     */
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

        // collect and sort topics for a cleaner display
        List<Topic> topics = new ArrayList<>(TopicManagerSingleton.get().getTopics());
        topics.sort(Comparator.comparing(t -> t.name));

        // create the html page
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");

        html.append("<style>");

        html.append("body{");
        html.append("margin:0;");
        html.append("font-family:Arial,sans-serif;");
        html.append("background:#f5f7fb;");
        html.append("color:#222;");
        html.append("}");

        html.append(".page{");
        html.append("padding:18px;");
        html.append("}");

        html.append(".card{");
        html.append("background:white;");
        html.append("border:1px solid #d0d7de;");
        html.append("border-radius:12px;");
        html.append("box-shadow:0 2px 8px rgba(0,0,0,0.08);");
        html.append("padding:18px;");
        html.append("}");

        html.append("h2{");
        html.append("margin-top:0;");
        html.append("margin-bottom:6px;");
        html.append("color:#1f4e79;");
        html.append("}");

        html.append(".subtitle{");
        html.append("color:#666;");
        html.append("font-size:14px;");
        html.append("margin-bottom:18px;");
        html.append("}");

        html.append(".summary{");
        html.append("margin-bottom:14px;");
        html.append("font-weight:bold;");
        html.append("color:#333;");
        html.append("}");

        html.append("table{");
        html.append("width:100%;");
        html.append("border-collapse:collapse;");
        html.append("overflow:hidden;");
        html.append("border-radius:10px;");
        html.append("}");

        html.append("th{");
        html.append("background:#1f4e79;");
        html.append("color:white;");
        html.append("padding:10px;");
        html.append("text-align:center;");
        html.append("}");

        html.append("td{");
        html.append("padding:10px;");
        html.append("text-align:center;");
        html.append("border-bottom:1px solid #e6e6e6;");
        html.append("}");

        html.append("tr:nth-child(even){");
        html.append("background:#f8fbff;");
        html.append("}");

        html.append("tr:hover{");
        html.append("background:#eef6ff;");
        html.append("}");

        html.append(".empty{");
        html.append("padding:30px;");
        html.append("text-align:center;");
        html.append("color:#777;");
        html.append("}");

        html.append("</style>");

        html.append("</head>");
        html.append("<body>");

        html.append("<div class=\"page\">");
        html.append("<div class=\"card\">");

        html.append("<h2>Topic Values</h2>");
        html.append("<div class=\"subtitle\">Current values stored in the Topic Manager</div>");
        html.append("<div class=\"summary\">Total Topics: ");
        html.append(topics.size());
        html.append("</div>");

        if(topics.isEmpty())
        {
            html.append("<div class=\"empty\">");
            html.append("No topics are currently available.");
            html.append("</div>");
        }
        else
        {
            html.append("<table>");

            html.append("<tr>");
            html.append("<th>Topic</th>");
            html.append("<th>Last Value</th>");
            html.append("</tr>");

            for(Topic t : topics)
            {
                html.append("<tr>");

                html.append("<td>");
                html.append(t.name);
                html.append("</td>");

                html.append("<td>");

                if(t.getLastMessage() != null)
                {
                    html.append(t.getLastMessage().asText);
                }
                else
                {
                    html.append("-");
                }

                html.append("</td>");

                html.append("</tr>");
            }

            html.append("</table>");
        }

        html.append("</div>");
        html.append("</div>");

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

    /**
     * No-op close hook for the topic display servlet.
     *
     * @throws IOException never thrown by this implementation
     */
    @Override
    public void close() throws IOException {
        // nothing to close
    }
}