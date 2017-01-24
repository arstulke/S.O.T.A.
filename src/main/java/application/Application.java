package application;

import game.util.GameReader;
import network.WebSocketHandler;
import spark.Spark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created application.Application.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
public class Application {
    public static GameReader gameReader;

    public static void main(String[] args) throws IOException {
        gameReader = new GameReader();

        Spark.port(80);
        Spark.staticFileLocation("/public");
        Spark.webSocket("/game", WebSocketHandler.class);
        Spark.get("/res", (request, response) -> gameReader.getResources(request.queryMap("map").value()));
        Spark.get("/maps", (request, response) -> {
            response.header("Content-type", "application/json");
            return gameReader.loadInstances().toString();
        });

        Spark.init();

        new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String line = br.readLine();
                    switch (line) {
                        case "reload":
                            gameReader.reload();
                            break;

                        case "stop":
                            System.exit(0);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
