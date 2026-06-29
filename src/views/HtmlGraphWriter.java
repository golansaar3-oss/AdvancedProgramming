package views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import configs.Graph;
import configs.Node;

public class HtmlGraphWriter {

    public static List<String> getGraphHTML(Graph graph) {

        List<String> html = new ArrayList<>();

        // svg size
        int width = 900;
        int height = 600;

        // place the nodes around a circle
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 220;

        // save location for each node
        Map<Node, Integer> xMap = new HashMap<>();
        Map<Node, Integer> yMap = new HashMap<>();

        int n = graph.size();

        for(int i = 0; i < n; i++)
        {
            Node node = graph.get(i);

            double angle = 2 * Math.PI * i / n;

            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));

            xMap.put(node, x);
            yMap.put(node, y);
        }

        html.add("<!DOCTYPE html>");
        html.add("<html>");
        html.add("<head>");
        html.add("<meta charset=\"UTF-8\">");
        html.add("<title>Graph</title>");
        html.add("</head>");
        html.add("<body>");

        html.add("<h2>Computational Graph</h2>");

        html.add("<svg width=\"" + width + "\" height=\"" + height + "\" style=\"border:1px solid black;\">");

        // define arrow head for graph edges
        html.add("<defs>");
        html.add("<marker id=\"arrow\" markerWidth=\"8\" markerHeight=\"8\" refX=\"7\" refY=\"4\" orient=\"auto\" markerUnits=\"strokeWidth\">");
        html.add("<path d=\"M0,0 L0,8 L8,4 z\" fill=\"black\" />");
        html.add("</marker>");
        html.add("</defs>");

        // draw edges first so nodes will be above them
        for(Node node : graph)
        {
            int x1 = xMap.get(node);
            int y1 = yMap.get(node);

            for(Node neighbor : node.getEdges())
            {
                Integer x2 = xMap.get(neighbor);
                Integer y2 = yMap.get(neighbor);

                if(x2 == null || y2 == null)
                {
                    continue;
                }

                // shorten the line so the arrow will not be hidden under the node
                double dx = x2 - x1;
                double dy = y2 - y1;
                double len = Math.sqrt(dx * dx + dy * dy);

                if(len == 0)
                {
                    continue;
                }

                // how much to stop before the target node
                int targetOffset = 38;

                double newX2 = x2 - (dx / len) * targetOffset;
                double newY2 = y2 - (dy / len) * targetOffset;

                html.add("<line x1=\"" + x1 + "\" y1=\"" + y1 +
                        "\" x2=\"" + (int)newX2 + "\" y2=\"" + (int)newY2 +
                        "\" stroke=\"black\" stroke-width=\"2\" marker-end=\"url(#arrow)\" />");
            }
        }

        // draw all nodes
        for(Node node : graph)
        {
            int x = xMap.get(node);
            int y = yMap.get(node);

            String name = node.getName();

            // first letter tells us the type: T = topic, A = agent
            boolean isTopic = name.startsWith("T");

            // remove the first letter before displaying the name
            String displayName = name;

            if(name.length() > 1)
            {
                displayName = name.substring(1);
            }

            if(isTopic)
            {
                // topic is a rectangle
                html.add("<rect x=\"" + (x - 45) + "\" y=\"" + (y - 25) +
                        "\" width=\"90\" height=\"50\" fill=\"lightblue\" stroke=\"black\" />");

                html.add("<text x=\"" + x + "\" y=\"" + (y + 5) +
                        "\" text-anchor=\"middle\" font-size=\"14\">" + displayName + "</text>");
            }
            else
            {
                // agent is a circle
                html.add("<circle cx=\"" + x + "\" cy=\"" + y +
                        "\" r=\"35\" fill=\"lightgreen\" stroke=\"black\" />");

                html.add("<text x=\"" + x + "\" y=\"" + (y + 5) +
                        "\" text-anchor=\"middle\" font-size=\"12\">" + displayName + "</text>");
            }
        }

        html.add("</svg>");

        html.add("<p>Has cycles: " + graph.hasCycles() + "</p>");

        html.add("</body>");
        html.add("</html>");

        return html;
    }
}