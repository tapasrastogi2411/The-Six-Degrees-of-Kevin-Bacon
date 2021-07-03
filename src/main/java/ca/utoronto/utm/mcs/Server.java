package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    // TODO Complete This Class
    private HttpServer server;

    @Inject
    public Server(HttpServer server){
        this.server = server; // Created this constructor for the server here
    }

    // The bind method needed to `bind` the server to the 0.0.0.0 port. As discussed in tutorial on REST APIs
    public void serverBind(int port) throws IOException {
        this.server.bind(new InetSocketAddress("localhost", port), 0);
    }

    public void context(ReqHandler reqHandler) {
        server.createContext("/api/v1/addActor", reqHandler);
        server.createContext("/api/v1/addMovie", reqHandler);
        server.createContext("/api/v1/addRelationship", reqHandler);
        server.createContext("/api/v1/getActor", reqHandler);
        server.createContext("/api/v1/hasRelationship", reqHandler);
        server.createContext("/api/v1/computeBaconNumber", reqHandler);
        server.createContext("/api/v1/computeBaconPath", reqHandler);
    }

    // A simple method which is called to start the server in `App.java`
    public void startServer() {
        this.server.start();
    }
}