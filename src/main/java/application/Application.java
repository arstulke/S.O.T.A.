package application;

import game.util.GameReader;
import network.WebSocketHandler;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * Created application.Application.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
public class Application {
    public static GameReader gameReader;

    public static void main(String[] args) {
        gameReader = new GameReader();

        Spark.port(80);
        Spark.staticFileLocation("/public");
        Spark.webSocket("/game", WebSocketHandler.class);
        Spark.get("/res", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                String mapName = request.queryMap("map").value();

                return gameReader.getResources(mapName);
            }
        });

        Spark.init();
    }
}
