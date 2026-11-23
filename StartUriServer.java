/*
 * Starts the BridgeDb URI web service (org.bridgedb.uri.ws.server) in an
 * embedded Jetty 9, without a war file, web.xml or Spring.
 *
 * Run it with start-uri-server.sh, which sets up the classpath. Arguments:
 * [port] [host], by default 8183 and localhost.
 */
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class StartUriServer {

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8183;
        String host = args.length > 1 ? args[1] : "localhost";

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        ServletHolder cxf = context.addServlet(CXFNonSpringJaxrsServlet.class, "/*");
        cxf.setInitParameter("jaxrs.serviceClasses", "org.bridgedb.ws.uri.WSUriServer");
        // one service object for all requests, so the database is set up only once
        cxf.setInitParameter("jaxrs.scope", "singleton");
        cxf.setInitOrder(1);
        server.setHandler(context);

        server.start();
        System.out.println("BridgeDb URI web service running at http://" + host + ":" + port + "/");
        server.join();
    }
}
