package server;

import servlets.*;

/**
 * HTTP server interface for handling web requests.
 * Supports registering servlets by HTTP command and URI pattern, with multi-threaded request handling.
 */
public interface HTTPServer extends Runnable {

    /**
     * Adds a servlet to handle requests that match the given HTTP command and URI prefix.
     *
     * @param httpCommanmd the HTTP command, for example GET, POST, or DELETE
     * @param uri the URI prefix that should be handled by this servlet
     * @param s the servlet that will handle matching requests
     */
    public void addServlet(String httpCommanmd, String uri, Servlet s);

    /**
     * Removes a servlet from the server.
     *
     * @param httpCommanmd the HTTP command of the servlet
     * @param uri the URI prefix of the servlet
     */
    public void removeServlet(String httpCommanmd, String uri);

    /**
     * Starts the server thread.
     */
    public void start();

    /**
     * Closes the server and releases its resources.
     */
    public void close();
}