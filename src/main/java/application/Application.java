package application;

import network.WebSocketHandler;
import spark.Spark;

/**
 * Created application.Application.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
public class Application {
    public static void main(String[] args) {
        Spark.port(80);
        Spark.staticFileLocation("/public");
        Spark.webSocket("/game", WebSocketHandler.class);

        Spark.init();
    }
}
