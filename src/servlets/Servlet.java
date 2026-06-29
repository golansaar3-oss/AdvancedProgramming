package servlets;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser.RequestInfo;

/**
 * Interface for handling HTTP requests and writing responses.
 * Implement this to create custom request handlers for the HTTP server.
 */
public interface Servlet {

    /**
     * Handles an HTTP request.
     *
     * @param ri the parsed request information
     * @param toClient the output stream used to send the response to the client
     * @throws IOException if writing the response fails
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;

    /**
     * Closes resources used by the servlet.
     *
     * @throws IOException if closing fails
     */
    void close() throws IOException;
}