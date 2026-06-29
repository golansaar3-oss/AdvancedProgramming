package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses HTTP requests from raw input streams.
 * Extracts the HTTP method, URI, parameters, headers, and body content into a structured RequestInfo object.
 */
public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {

        String firstLine = reader.readLine();

        if (firstLine == null || firstLine.isEmpty()) {
            return null;
        }

        String[] firstLineParts = firstLine.split(" ");

        if (firstLineParts.length < 2) {
            return null;
        }

        String httpCommand = firstLineParts[0];
        String uri = firstLineParts[1];

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        String path = uri;
        String query = "";

        int queryIndex = uri.indexOf('?');

        if (queryIndex != -1) {
            path = uri.substring(0, queryIndex);
            query = uri.substring(queryIndex + 1);
        }

        parseQueryParameters(query, parameters);

        String cleanPath = path;

        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        String[] uriSegments;

        if (cleanPath.isEmpty()) {
            uriSegments = new String[0];
        } else {
            uriSegments = cleanPath.split("/");
        }

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }

            int colonIndex = line.indexOf(':');

            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key.toLowerCase(), value);
            }
        }

        int contentLength = 0;

        if (headers.containsKey("content-length")) {
            try {
                contentLength = Integer.parseInt(headers.get("content-length"));
            } catch (NumberFormatException e) {
                contentLength = 0;
            }
        }

        byte[] content = new byte[0];

        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];

            int totalRead = 0;

            while (totalRead < contentLength) {
                int read = reader.read(bodyChars, totalRead, contentLength - totalRead);

                if (read == -1) {
                    break;
                }

                totalRead += read;
            }

            String body = new String(bodyChars, 0, totalRead);

            parseBodyParameters(body, parameters);

            content = body.getBytes(StandardCharsets.UTF_8);
        }

        return new RequestInfo(
                httpCommand,
                uri,
                uriSegments,
                parameters,
                content
        );
    }

    private static void parseQueryParameters(String query, Map<String, String> parameters) {
        if (query == null || query.isEmpty()) {
            return;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            int equalIndex = pair.indexOf('=');

            if (equalIndex != -1) {
                String key = pair.substring(0, equalIndex);
                String value = pair.substring(equalIndex + 1);
                parameters.put(urlDecode(key), urlDecode(value));
            }
        }
    }

    private static void parseBodyParameters(String body, Map<String, String> parameters) {
        if (body == null || body.isEmpty()) {
            return;
        }

        // Handles normal form body:
        // config=someText&other=value
        if (body.contains("=") && body.contains("&")) {
            parseQueryParameters(body, parameters);
            return;
        }

        if (body.startsWith("config=")) {
            parseQueryParameters(body, parameters);
            return;
        }

        // Handles old assignment style:
        // filename="hello_world.txt"
        //
        // content...
        String[] lines = body.split("\\R");

        for (String line : lines) {
            int equalIndex = line.indexOf('=');

            if (equalIndex != -1) {
                String key = line.substring(0, equalIndex).trim();
                String value = line.substring(equalIndex + 1).trim();

                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }

                parameters.put(key, value);
            }
        }
    }

    private static String urlDecode(String s) {
        try {
            return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    /**
     * Contains parsed information from an HTTP request (method, URI, parameters, headers, body).
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}