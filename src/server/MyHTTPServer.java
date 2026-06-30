package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.RequestParser.RequestInfo;
import servlets.*;

/**
 * Multi-threaded HTTP server implementation using a thread pool.
 * Matches requests to servlets using longest URI prefix matching and handles each request in a background thread.
 */
public class MyHTTPServer extends Thread implements HTTPServer{
    private final int port;
    private final ExecutorService pool;

    private ServerSocket serverSocket;
    private volatile boolean stop;

    private final Map<String,Servlet> getServlets;
    private final Map<String,Servlet> postServlets;
    private final Map<String,Servlet> deleteServlets;

    /**
     * Creates a server bound to the given port and backed by a fixed thread pool.
     *
     * @param port the listening port
     * @param nThreads the number of worker threads
     */
    public MyHTTPServer(int port,int nThreads){
        this.port = port;
        this.pool = Executors.newFixedThreadPool(nThreads);

        this.getServlets = new ConcurrentHashMap<>();
        this.postServlets = new ConcurrentHashMap<>();
        this.deleteServlets = new ConcurrentHashMap<>();

        this.stop = false;
    }

    /**
     * Registers a servlet for the given HTTP command and URI prefix.
     *
     * @param httpCommanmd the HTTP method name
     * @param uri the URI prefix
     * @param s the servlet to register
     */
    public void addServlet(String httpCommanmd, String uri, Servlet s){
        if(httpCommanmd.equalsIgnoreCase("GET"))
        {
            getServlets.put(uri, s);
        }

        if(httpCommanmd.equalsIgnoreCase("POST"))
        {
            postServlets.put(uri, s);
        }

        if(httpCommanmd.equalsIgnoreCase("DELETE"))
        {
            deleteServlets.put(uri, s);
        }
    }

    /**
     * Removes a servlet registration for the given HTTP command and URI prefix.
     *
     * @param httpCommanmd the HTTP method name
     * @param uri the URI prefix
     */
    public void removeServlet(String httpCommanmd, String uri){
         if (httpCommanmd.equalsIgnoreCase("GET")) {
            getServlets.remove(uri);
        }

        if (httpCommanmd.equalsIgnoreCase("POST")) {
            postServlets.remove(uri);
        }

        if (httpCommanmd.equalsIgnoreCase("DELETE")) {
            deleteServlets.remove(uri);
        }
    }

    /**
     * Accepts client connections and dispatches requests to the worker pool.
     */
    @Override
public void run() {
    try {
        serverSocket = new ServerSocket(port);
        System.out.println("Server socket opened on port " + port);

        // accept() will wait at most 1 second, then throw SocketTimeoutException.
        // This lets the loop check the stop flag once per second.
        serverSocket.setSoTimeout(1000);

        while (!stop) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");
                

                // Each client is handled by the thread pool.
                pool.execute(() -> handleClient(client));

            } catch (SocketTimeoutException e) {
                // Normal situation: no client connected during this second.
                // Continue loop so we can check stop flag.
            }
        }

    } catch (Exception e) {
        System.out.println("Server failed:");
        e.printStackTrace();
    }
}

    private void handleClient(Socket client) {
    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        RequestInfo ri = RequestParser.parseRequest(reader);
        

        if (ri == null) {
            System.out.println("RequestInfo is null");
            client.close();
            return;
        }
        System.out.println("Client request: " + ri.getHttpCommand() + " " + ri.getUri());

        Servlet servlet = getMatchingServlet(ri.getHttpCommand(), ri.getUri());


        if (servlet != null) {
            servlet.handle(ri, client.getOutputStream());
        } else {
            String body = "<html><body><h2>404 No matching servlet</h2></body></html>";
            byte[] bodyBytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            String response =
                    "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + bodyBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            client.getOutputStream().write(response.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            client.getOutputStream().write(bodyBytes);
            client.getOutputStream().flush();
        }

        client.close();

    } catch (java.net.SocketException e) {
        System.out.println("Client disconnected before response was fully sent: " + e.getMessage());

        try {
            client.close();
        } catch (Exception ex) {
        }

    } catch (Exception e) {
        System.out.println("Client handling failed:");
        e.printStackTrace();

        try {
            client.close();
        } catch (Exception ex) {
        }
    }
    }

    /**
     * Selects the servlet whose URI prefix best matches the request.
     *
     * @param command the HTTP method name
     * @param uri the request URI
     * @return the matching servlet, or null if none matches
     */
    private Servlet getMatchingServlet(String command, String uri)
    {
        Map<String, Servlet> map = null;

        if (command.equalsIgnoreCase("GET")) {
            map = getServlets;
        }

        if (command.equalsIgnoreCase("POST")) {
            map = postServlets;
        }

        if (command.equalsIgnoreCase("DELETE")) {
            map = deleteServlets;
        }

        if (map == null) {
            return null;
        }

        String bestMatch = null;

        for (String servletUri : map.keySet()) {

            // longest prefix match
            if (uri.startsWith(servletUri)) {

                if (bestMatch == null || servletUri.length() > bestMatch.length()) {
                    bestMatch = servletUri;
                }
            }
        }

        if (bestMatch == null) {
            return null;
        }

        return map.get(bestMatch);
    }

    /**
     * Stops the server, closes the socket, and shuts down all registered servlets.
     */
    public void close(){
        stop = true;

        try{
            if(serverSocket !=null)
            {
                serverSocket.close();
            }
        } catch(Exception e){}

        pool.shutdownNow();
        closeServlets(getServlets);
        closeServlets(postServlets);
        closeServlets(deleteServlets);
    }

    /**
     * Closes every servlet registered in the provided map.
     *
     * @param map the servlet registry to close
     */
    private void closeServlets(Map<String, Servlet> map) {
        for (Servlet s : map.values()) {
            try {
                s.close();
            } catch (Exception e) {
            }
        }
    }
}
