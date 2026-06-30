import server.*;
import servlets.*;


/**
 * Application entry point that wires the HTTP server and the demo servlets.
 */
public class Main {
    /**
     * Starts the demo server, registers the built-in servlets, and waits for shutdown.
     *
     * @param args command-line arguments; currently unused
     * @throws Exception if server startup or shutdown fails
     */
    public static void main(String[] args) throws Exception{

        
        HTTPServer server = new MyHTTPServer(8080,5);

        server.addServlet("GET", "/publish", new TopicDisplayer());
        server.addServlet("POST", "/upload", new ConfLoader()); 
        server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
        
        System.out.println("Server started");
        server.start(); 
        System.in.read(); 
        server.close(); 
        System.out.println("done");
        
    }
}