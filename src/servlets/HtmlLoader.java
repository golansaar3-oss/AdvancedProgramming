package servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import server.RequestParser.RequestInfo;

/**
 * Serves static files (HTML, CSS, JavaScript) from a specified directory.
 * Protects against directory traversal attacks and serves index.html for directory requests.
 */
public class HtmlLoader implements Servlet {

    private final Path rootFolder;

    /**
     * Creates a static file servlet rooted at the given folder.
     *
     * @param rootFolder the root folder for served files
     */
    public HtmlLoader(String rootFolder) {
        this.rootFolder = Paths.get(rootFolder).toAbsolutePath().normalize();
    }

    /**
     * Serves a static file response for the requested URI.
     *
     * @param ri the parsed request information
     * @param toClient the client output stream
     * @throws IOException if reading the file or writing the response fails
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String requestedFile = getRequestedFile(ri);


        if (requestedFile.contains("..")) {
            writeHtmlResponse(toClient, 403, "<html><body><h2>403 Forbidden</h2></body></html>");
            return;
        }

        Path filePath = rootFolder.resolve(requestedFile).normalize();


        if (!filePath.startsWith(rootFolder)) {
            writeHtmlResponse(toClient, 403, "<html><body><h2>403 Forbidden</h2></body></html>");
            return;
        }

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            writeHtmlResponse(toClient, 404, "<html><body><h2>404 File Not Found</h2><p>" 
                    + escapeHtml(filePath.toString()) + "</p></body></html>");
            return;
        }

        String html = Files.readString(filePath, StandardCharsets.UTF_8);
        writeHtmlResponse(toClient, 200, html);
    }

    /**
     * Resolves the requested file path relative to the servlet root.
     *
     * @param ri the parsed request information
     * @return the requested relative file path
     */
    private String getRequestedFile(RequestInfo ri) {
        String[] segments = ri.getUriSegments();

        if (segments.length <= 1) {
            return "index.html";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < segments.length; i++) {
            if (i > 1) {
                sb.append("/");
            }
            sb.append(segments[i]);
        }

        String result = sb.toString();

        if (result.isEmpty()) {
            return "index.html";
        }

        return result;
    }

    /**
     * Writes a complete HTML response with the given status code.
     *
     * @param out the client output stream
     * @param statusCode the HTTP status code
     * @param body the HTML body
     * @throws IOException if writing fails
     */
    private void writeHtmlResponse(OutputStream out, int statusCode, String body) throws IOException {
        String statusText = statusCode == 200 ? "OK" :
                            statusCode == 403 ? "Forbidden" :
                            statusCode == 404 ? "Not Found" :
                            "Error";

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        String header =
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }

    /**
     * Escapes HTML special characters in text content.
     *
     * @param s the text to escape
     * @return the escaped text
     */
    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * No-op close hook for the static file servlet.
     *
     * @throws IOException never thrown by this implementation
     */
    @Override
    public void close() throws IOException {
        // Nothing to close
    }
}