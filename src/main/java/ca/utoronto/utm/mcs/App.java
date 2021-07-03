package ca.utoronto.utm.mcs;

import java.io.IOException;

public class App
{
    static int port = 8080;

    public static void main(String[] args) throws IOException
    {
        // TODO Create Your Server Context Here, There Should Only Be One Context

        // Creating a Dagger Dependency injection similar to the videos linked in the handout
        ServerComponent serverComponent = DaggerServerComponent.create();
        ReqHandlerComponent reqHandlerComponent = DaggerReqHandlerComponent.create();

        // Using the Dagger Component to build server using buildServer() method initiated in ServerComponent.java
        Server server = serverComponent.buildServer();
        ReqHandler reqHandler = reqHandlerComponent.buildHandler();

        // Called to `bind` the aforementioned `port` to 0.0.0.0 using InetSocketAddress as mentioned in tutorial
        server.serverBind(port);
        server.context(reqHandler);

        // To initially start the server without `ReqHandler`, will change to .createContext() soon
        server.startServer();

        System.out.printf("Server started on port %d\n", port);
    }
}