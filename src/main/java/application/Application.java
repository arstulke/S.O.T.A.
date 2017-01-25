package application;

import game.util.GameReader;
import network.WebSocketHandler;
import org.json.JSONArray;
import spark.Spark;
import java.io.*;
import java.util.Set;

import static spark.Spark.*;

/**
 * Created application.Application.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
public class Application {
    public static GameReader gameReader;

    public static void main(String[] args) throws IOException {
        gameReader = new GameReader();
        gameReader.reload(true);

        port(80);
        staticFileLocation("/public");
        webSocket("/game", WebSocketHandler.class);
        get("/resources", (request, response) -> {
            String map = request.queryMap("map").value();
            String mode = request.queryMap("mode").value();

            Set<String> resources = gameReader.getResources(map, mode);

            JSONArray arr = new JSONArray();
            resources.forEach(s -> arr.put(arr.length(), s));

            response.header("Content-type", "application/json");
            return arr.toString();
        });
        get("/maps", (request, response) -> {
            response.header("Content-type", "application/json");
            return gameReader.loadInstances().toString();
        });

        get("/upload", (request, response) -> "<form method=\"post\" action=\"/upload\" enctype=\"multipart/form-data\">\n" +
                "    <label>Wählen Sie eine ZIP-Datei aus\n" +
                "        <input name=\"file\" type=\"file\" size=\"50\" accept=\".zip\">\n" +
                "    </label>\n" +
                "    <button>... und ab geht die Post!</button>\n" +
                "</form>");
        post("/upload", new MapUploadRoute());

        Spark.init();

        new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String line = br.readLine();
                    switch (line) {
                        case "reload":
                            gameReader.reload(true);
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
