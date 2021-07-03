package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;

import java.io.IOException;

@Module
public class ServerModule {
    // TODO Complete This Module

    /*ServerComponent file looks at this ServerModule.java file to provide the below mentioned HttpServer
     dependency. This is done since `Server.java` constructor has a `HttpServer` dependency */
    @Provides
    HttpServer provideServer(){
        try {
            return HttpServer.create();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}