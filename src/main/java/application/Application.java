package application;

import game.util.GameLoader;
import network.WebSocketHandler;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spark.Spark.*;

/**
 * Created application.Application.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
public class Application {
    public static GameLoader gameloader = new GameLoader();
    static TextureLoader textureLoader = new TextureLoader();

    public static void main(String[] args) throws IOException {
        gameloader.reload(true, true);
        textureLoader.reload();

        port(80);
        staticFileLocation("/public");
        webSocket("/game", WebSocketHandler.class);

        get("/resources", new ResourceRoute());
        get("/maps", (request, response) -> {
            response.header("Content-type", "application/json");
            return gameloader.loadInstances(true, false).toString();
        });

        get("/upload", (request, response) -> "<form method=\"post\" action=\"/upload\" enctype=\"multipart/form-data\">\n" +
                "    <label>Wählen Sie eine ZIP-Datei aus\n" +
                "        <input name=\"file\" type=\"file\" size=\"50\" accept=\".zip\">\n" +
                "    </label>\n" +
                "    <button>... und ab geht die Post!</button>\n" +
                "</form>");
        post("/upload", new MapUploadRoute());

        get("/texture", (request, response) -> {
            String map = request.queryMap("map").value();
            String name = request.queryMap("name").value();

            response.raw().setContentType("image/png");
            try (OutputStream out = response.raw().getOutputStream()) {
                ImageIO.write(textureLoader.getTexture(map, name), "png", out);
            }
            return response;
        });

        Spark.init();

        new CommandLine().start();
    }
}
