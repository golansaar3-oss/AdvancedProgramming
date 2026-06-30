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

    /**
     * Utility class; do not instantiate.
     */
    private RequestParser() {
    }

    /**
     * Parses a raw HTTP request into a structured request object.
     *
     * @param reader the request reader
     * @return the parsed request, or null when the input is empty or invalid
     * @throws IOException if reading from the stream fails
     */
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

    /**
     * Decodes URL query parameters into the parameter map.
     *
     * @param query the raw query string
     * @param parameters the parameter map to populate
     */
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

    /**
     * Parses request body content into the parameter map.
     *
     * @param body the request body
     * @param parameters the parameter map to populate
     */
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

    /**
     * Decodes a single URL-encoded string value.
     *
     * @param s the encoded value
     * @return the decoded value, or the original text if decoding fails
     */
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

        /**
         * Creates a parsed request record.
         *
         * @param httpCommand the HTTP command
         * @param uri the raw request URI
         * @param uriSegments the URI path segments
         * @param parameters the parsed parameters
         * @param content the request body bytes
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        /**
         * Returns the HTTP command.
         *
         * @return the HTTP command
         */
        public String getHttpCommand() {
            return httpCommand;
        }

        /**
         * Returns the raw URI.
         *
         * @return the raw URI
         */
        public String getUri() {
            return uri;
        }

        /**
         * Returns the URI path segments.
         *
         * @return the URI path segments
         */
        public String[] getUriSegments() {
            return uriSegments;
        }

        /**
         * Returns the parsed parameters.
         *
         * @return the parameters map
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Returns the request body bytes.
         *
         * @return the request content bytes
         */
        public byte[] getContent() {
            return content;
        }
    }
}