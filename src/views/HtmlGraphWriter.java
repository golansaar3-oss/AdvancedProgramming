package views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import configs.Graph;
import configs.Node;


/**
 * Creates an HTML/SVG visualization for a computational graph.
 * Topics are shown as rectangles and agents are shown as circles.
 */
public class HtmlGraphWriter {
    /**
     * Utility class; do not instantiate.
     */
    private HtmlGraphWriter() {
    }

    /**
     * Builds the full HTML document used to visualize the computational graph.
     *
     * @param graph the graph to render
     * @return the HTML document lines
     */
    public static List<String> getGraphHTML(Graph graph) {

        List<String> html = new ArrayList<>();

        int width = 900;
        int height = 600;

        int centerX = width / 2;
        int centerY = height / 2;
        int radius = 220;

        Map<Node, Integer> xMap = new HashMap<>();
        Map<Node, Integer> yMap = new HashMap<>();
        Map<Node, String> idMap = new HashMap<>();

        int n = graph.size();

        for(int i = 0; i < n; i++)
        {
            Node node = graph.get(i);

            double angle = 2 * Math.PI * i / n;

            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));

            xMap.put(node, x);
            yMap.put(node, y);
            idMap.put(node, "node" + i);
        }

        String equation = buildEquation(graph);

        html.add("<!DOCTYPE html>");
        html.add("<html>");
        html.add("<head>");
        html.add("<meta charset=\"UTF-8\">");
        html.add("<title>Graph</title>");

        html.add("<style>");
        html.add("body {");
        html.add("    margin: 0;");
        html.add("    font-family: Arial, sans-serif;");
        html.add("    background: #f5f7fb;");
        html.add("    color: #222;");
        html.add("}");
        html.add(".graph-page {");
        html.add("    padding: 18px;");
        html.add("}");
        html.add("h2 {");
        html.add("    margin: 0 0 8px 0;");
        html.add("}");
        html.add(".hint {");
        html.add("    margin: 0 0 14px 0;");
        html.add("    color: #666;");
        html.add("    font-size: 14px;");
        html.add("}");
        html.add("svg {");
        html.add("    background: white;");
        html.add("    border: 1px solid #d0d7de;");
        html.add("    border-radius: 12px;");
        html.add("    box-shadow: 0 2px 8px rgba(0,0,0,0.08);");
        html.add("}");
        html.add(".edge {");
        html.add("    stroke: #444;");
        html.add("    stroke-width: 2;");
        html.add("}");
        html.add(".node {");
        html.add("    cursor: move;");
        html.add("}");
        html.add(".topic-rect {");
        html.add("    fill: #d9ecff;");
        html.add("    stroke: #2f6f9f;");
        html.add("    stroke-width: 2;");
        html.add("}");
        html.add(".agent-circle {");
        html.add("    fill: #ddf8df;");
        html.add("    stroke: #3b8b45;");
        html.add("    stroke-width: 2;");
        html.add("}");
        html.add(".node-text {");
        html.add("    font-size: 13px;");
        html.add("    font-weight: bold;");
        html.add("    pointer-events: none;");
        html.add("}");
        html.add(".cycle-status {");
        html.add("    margin-top: 10px;");
        html.add("    font-weight: bold;");
        html.add("}");
        html.add(".equation-card {");
        html.add("    margin-top: 14px;");
        html.add("    background: white;");
        html.add("    border: 1px solid #d0d7de;");
        html.add("    border-radius: 12px;");
        html.add("    padding: 14px 16px;");
        html.add("    box-shadow: 0 2px 8px rgba(0,0,0,0.08);");
        html.add("}");
        html.add(".equation-title {");
        html.add("    margin: 0 0 8px 0;");
        html.add("    font-size: 15px;");
        html.add("    font-weight: bold;");
        html.add("    color: #333;");
        html.add("}");
        html.add(".equation-text {");
        html.add("    margin: 0;");
        html.add("    font-size: 18px;");
        html.add("    font-family: Consolas, monospace;");
        html.add("    color: #1f4e79;");
        html.add("}");
        html.add("</style>");

        html.add("</head>");
        html.add("<body>");
        html.add("<div class=\"graph-page\">");

        html.add("<h2>Computational Graph</h2>");
        html.add("<p class=\"hint\">Drag the nodes to rearrange the graph.</p>");

        html.add("<svg id=\"graphSvg\" width=\"" + width + "\" height=\"" + height + "\">");

        html.add("<defs>");
        html.add("<marker id=\"arrow\" markerWidth=\"10\" markerHeight=\"10\" refX=\"9\" refY=\"3\" orient=\"auto\" markerUnits=\"strokeWidth\">");
        html.add("<path d=\"M0,0 L0,6 L9,3 z\" fill=\"#444\" />");
        html.add("</marker>");
        html.add("</defs>");

        // Draw edges first so the nodes appear above them.
        int edgeIndex = 0;

        for(Node node : graph)
        {
            String fromId = idMap.get(node);

            for(Node neighbor : node.getEdges())
            {
                String toId = idMap.get(neighbor);

                if(toId == null)
                {
                    continue;
                }

                html.add("<line id=\"edge" + edgeIndex + "\" class=\"edge\" " +
                        "data-from=\"" + fromId + "\" data-to=\"" + toId + "\" " +
                        "marker-end=\"url(#arrow)\" />");

                edgeIndex++;
            }
        }

        // Draw all nodes as groups. The whole group is draggable.
        for(Node node : graph)
        {
            int x = xMap.get(node);
            int y = yMap.get(node);

            String id = idMap.get(node);
            String name = node.getName();

            boolean isTopic = name.startsWith("T");

            String displayName = cleanName(name);

            html.add("<g id=\"" + id + "\" class=\"node\" data-x=\"" + x + "\" data-y=\"" + y + "\" transform=\"translate(" + x + "," + y + ")\">");

            if(isTopic)
            {
                html.add("<rect class=\"topic-rect\" x=\"-45\" y=\"-25\" width=\"90\" height=\"50\" rx=\"10\" ry=\"10\" />");
                html.add("<text class=\"node-text\" x=\"0\" y=\"5\" text-anchor=\"middle\">" + escapeHtml(displayName) + "</text>");
            }
            else
            {
                html.add("<circle class=\"agent-circle\" cx=\"0\" cy=\"0\" r=\"36\" />");
                html.add("<text class=\"node-text\" x=\"0\" y=\"5\" text-anchor=\"middle\">" + escapeHtml(displayName) + "</text>");
            }

            html.add("</g>");
        }

        html.add("</svg>");

        html.add("<p class=\"cycle-status\">Has cycles: " + graph.hasCycles() + "</p>");

        html.add("<div class=\"equation-card\">");
        html.add("<p class=\"equation-title\">Computed Equation</p>");
        html.add("<p class=\"equation-text\">" + escapeHtml(equation) + "</p>");
        html.add("</div>");

        html.add("<script>");
        html.add("let selectedNode = null;");
        html.add("let offsetX = 0;");
        html.add("let offsetY = 0;");
        html.add("");
        html.add("const svg = document.getElementById('graphSvg');");
        html.add("const nodes = document.querySelectorAll('.node');");
        html.add("");
        html.add("nodes.forEach(function(node) {");
        html.add("    node.addEventListener('mousedown', startDrag);");
        html.add("});");
        html.add("");
        html.add("svg.addEventListener('mousemove', drag);");
        html.add("svg.addEventListener('mouseup', endDrag);");
        html.add("svg.addEventListener('mouseleave', endDrag);");
        html.add("");
        html.add("function getMousePosition(event) {");
        html.add("    const point = svg.createSVGPoint();");
        html.add("    point.x = event.clientX;");
        html.add("    point.y = event.clientY;");
        html.add("    return point.matrixTransform(svg.getScreenCTM().inverse());");
        html.add("}");
        html.add("");
        html.add("function startDrag(event) {");
        html.add("    selectedNode = event.currentTarget;");
        html.add("    const mouse = getMousePosition(event);");
        html.add("    offsetX = mouse.x - parseFloat(selectedNode.dataset.x);");
        html.add("    offsetY = mouse.y - parseFloat(selectedNode.dataset.y);");
        html.add("}");
        html.add("");
        html.add("function drag(event) {");
        html.add("    if (selectedNode == null) {");
        html.add("        return;");
        html.add("    }");
        html.add("");
        html.add("    const mouse = getMousePosition(event);");
        html.add("    const newX = mouse.x - offsetX;");
        html.add("    const newY = mouse.y - offsetY;");
        html.add("");
        html.add("    selectedNode.dataset.x = newX;");
        html.add("    selectedNode.dataset.y = newY;");
        html.add("    selectedNode.setAttribute('transform', 'translate(' + newX + ',' + newY + ')');");
        html.add("");
        html.add("    updateEdges();");
        html.add("}");
        html.add("");
        html.add("function endDrag() {");
        html.add("    selectedNode = null;");
        html.add("}");
        html.add("");
        html.add("function updateEdges() {");
        html.add("    const edges = document.querySelectorAll('.edge');");
        html.add("");
        html.add("    edges.forEach(function(edge) {");
        html.add("        const fromNode = document.getElementById(edge.dataset.from);");
        html.add("        const toNode = document.getElementById(edge.dataset.to);");
        html.add("");
        html.add("        if (fromNode == null || toNode == null) {");
        html.add("            return;");
        html.add("        }");
        html.add("");
        html.add("        const x1 = parseFloat(fromNode.dataset.x);");
        html.add("        const y1 = parseFloat(fromNode.dataset.y);");
        html.add("        const x2 = parseFloat(toNode.dataset.x);");
        html.add("        const y2 = parseFloat(toNode.dataset.y);");
        html.add("");
        html.add("        const dx = x2 - x1;");
        html.add("        const dy = y2 - y1;");
        html.add("        const len = Math.sqrt(dx * dx + dy * dy);");
        html.add("");
        html.add("        if (len === 0) {");
        html.add("            return;");
        html.add("        }");
        html.add("");
        html.add("        // Start and end offsets keep the arrow outside the node shapes.");
        html.add("        const startOffset = 42;");
        html.add("        const endOffset = 46;");
        html.add("");
        html.add("        const newX1 = x1 + (dx / len) * startOffset;");
        html.add("        const newY1 = y1 + (dy / len) * startOffset;");
        html.add("        const newX2 = x2 - (dx / len) * endOffset;");
        html.add("        const newY2 = y2 - (dy / len) * endOffset;");
        html.add("");
        html.add("        edge.setAttribute('x1', newX1);");
        html.add("        edge.setAttribute('y1', newY1);");
        html.add("        edge.setAttribute('x2', newX2);");
        html.add("        edge.setAttribute('y2', newY2);");
        html.add("    });");
        html.add("}");
        html.add("");
        html.add("updateEdges();");
        html.add("</script>");

        html.add("</div>");
        html.add("</body>");
        html.add("</html>");

        return html;
    }

    /**
     * Builds a readable equation from the graph structure.
     * The method uses the edges between topics and agents, so it reflects the loaded config.
     */
    /**
     * Builds a readable equation summary from the graph topology.
     *
     * @param graph the graph to analyze
     * @return a rendered equation or a fallback message
     */
    private static String buildEquation(Graph graph) {

        Map<Node, List<Node>> agentInputs = new HashMap<>();
        Map<Node, List<Node>> agentOutputs = new HashMap<>();
        Map<Node, Node> topicProducer = new HashMap<>();
        List<Node> finalTopics = new ArrayList<>();

        for(Node node : graph)
        {
            if(isAgent(node))
            {
                agentInputs.put(node, new ArrayList<Node>());
                agentOutputs.put(node, new ArrayList<Node>());
            }
        }

        for(Node node : graph)
        {
            for(Node neighbor : node.getEdges())
            {
                if(isTopic(node) && isAgent(neighbor))
                {
                    List<Node> inputs = agentInputs.get(neighbor);

                    if(inputs != null)
                    {
                        inputs.add(node);
                    }
                }
                else if(isAgent(node) && isTopic(neighbor))
                {
                    List<Node> outputs = agentOutputs.get(node);

                    if(outputs != null)
                    {
                        outputs.add(neighbor);
                    }

                    topicProducer.put(neighbor, node);
                }
            }
        }

        for(Node node : graph)
        {
            if(isTopic(node) && node.getEdges().isEmpty() && topicProducer.containsKey(node))
            {
                finalTopics.add(node);
            }
        }

        if(finalTopics.isEmpty())
        {
            for(Node node : graph)
            {
                if(isTopic(node) && topicProducer.containsKey(node))
                {
                    finalTopics.add(node);
                }
            }
        }

        if(finalTopics.isEmpty())
        {
            return "No computed equation found";
        }

        StringBuilder equation = new StringBuilder();

        for(int i = 0; i < finalTopics.size(); i++)
        {
            Node topic = finalTopics.get(i);

            if(i > 0)
            {
                equation.append(" , ");
            }

            equation.append(cleanName(topic.getName()));
            equation.append(" = ");
            equation.append(buildTopicExpression(topic, topicProducer, agentInputs, new HashSet<Node>()));
        }

        return equation.toString();
    }

    /**
     * Builds the expression for a specific topic.
     * Input topics stay as variables, and produced topics are expanded recursively.
     */
    private static String buildTopicExpression(Node topic,
                                               Map<Node, Node> topicProducer,
                                               Map<Node, List<Node>> agentInputs,
                                               Set<Node> visiting) {

        if(visiting.contains(topic))
        {
            return cleanName(topic.getName());
        }

        Node producer = topicProducer.get(topic);

        if(producer == null)
        {
            return cleanName(topic.getName());
        }

        visiting.add(topic);

        List<Node> inputs = agentInputs.get(producer);
        String agentName = cleanName(producer.getName());

        if(inputs == null || inputs.isEmpty())
        {
            visiting.remove(topic);
            return cleanName(topic.getName());
        }

        String expression;

        if(agentName.contains("IncAgent"))
        {
            String x = buildTopicExpression(inputs.get(0), topicProducer, agentInputs, visiting);
            expression = "(" + x + " + 1)";
        }
        else if(agentName.contains("PlusAgent"))
        {
            expression = buildBinaryExpression("+", inputs, topicProducer, agentInputs, visiting);
        }
        else if(agentName.contains("MinAgent") || agentName.contains("MinusAgent"))
        {
            expression = buildBinaryExpression("-", inputs, topicProducer, agentInputs, visiting);
        }
        else if(agentName.contains("MulAgent"))
        {
            expression = buildBinaryExpression("*", inputs, topicProducer, agentInputs, visiting);
        }
        else
        {
            expression = agentName + "(...)";
        }

        visiting.remove(topic);
        return expression;
    }

    /**
     * Builds an expression for agents that use two input topics.
     */
    private static String buildBinaryExpression(String operator,
                                                List<Node> inputs,
                                                Map<Node, Node> topicProducer,
                                                Map<Node, List<Node>> agentInputs,
                                                Set<Node> visiting) {

        if(inputs.size() < 2)
        {
            return "?";
        }

        String x = buildTopicExpression(inputs.get(0), topicProducer, agentInputs, visiting);
        String y = buildTopicExpression(inputs.get(1), topicProducer, agentInputs, visiting);

        return "(" + x + " " + operator + " " + y + ")";
    }

    /**
     * Checks if a node represents a topic.
     */
    private static boolean isTopic(Node node) {
        return node.getName().startsWith("T");
    }

    /**
     * Checks if a node represents an agent.
     */
    private static boolean isAgent(Node node) {
        return node.getName().startsWith("A");
    }

    /**
     * Removes the internal graph prefix from a node name.
     */
    private static String cleanName(String name) {

        if(name.length() > 1)
        {
            return name.substring(1);
        }

        return name;
    }

    /**
     * Escapes text before inserting it into HTML.
     */
    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}