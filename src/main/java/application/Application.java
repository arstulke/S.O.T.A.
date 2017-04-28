package application;

import game.util.ApplicationProperties;
import game.util.GameLoader;
import network.WebSocketHandler;
import org.eclipse.jetty.util.log.Log;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;

import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;
import static spark.Spark.*;

/**
 * Created application.Application.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
public class Application {
    private final static ApplicationProperties properties = new ApplicationProperties();
    public final static GameLoader gameloader = new GameLoader();
    private final static TextureLoader textureLoader = new TextureLoader();

    public static void main(String[] args) throws IOException {
        prepareFileEnvironment();

        setPort();
        staticFileLocation("/public");
        webSocket("/game", WebSocketHandler.class);

        get("/maps", (request, response) -> {
            response.header("Content-type", "application/json");
            return gameloader.loadInstances().toString();
        });

        post("/file-upload", new MapUploadRoute());

        get("/resources", new ResourceRoute());
        get("/textures", (request, response) -> {
            String map = request.queryMap("map").value();
            String name = request.queryMap("name").value();

            response.raw().setContentType("image/png");
            try (OutputStream out = response.raw().getOutputStream()) {
                BufferedImage texture = textureLoader.getTexture(map, name);
                if (texture == null) {
                    texture = read(Application.class.getClassLoader().getResourceAsStream("public/error.png"));
                }
                write(texture, "png", out);
            } catch (IOException ignored) {
            }
            return response;
        });
        init();

        new CommandLine().start();
    }

    private static void prepareFileEnvironment() {
        Path unverifiedMapsPath = properties.getUnverifiedMapsPath();
        Path verifiedMapsPath = properties.getVerifiedMapsPath();

        File unverifiedMaps = unverifiedMapsPath.toFile();
        if (!unverifiedMaps.exists()) {
            unverifiedMaps.mkdir();
        }

        File verifiedMaps = verifiedMapsPath.toFile();
        if (!verifiedMaps.exists()) {
            verifiedMaps.mkdir();
        }

        String path1 = unverifiedMapsPath.relativize(verifiedMapsPath).toString();
        String path2 = verifiedMapsPath.relativize(unverifiedMapsPath).toString();
        if (path1.endsWith("..") || path2.endsWith("..")) {
            Log.getLogger(Application.class).warn("You aren't allowed to set a map folder as a child of another map folder.");
            System.exit(-1);
        }
    }

    private static void setPort() {
        int port = 80;
        try {
            port = new ApplicationProperties().getPort();
        } catch (NumberFormatException e) {
            Log.getLogger(Application.class).warn("You have to set a number in the properties.");
            System.exit(-1);
        }

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", port), 10);
            socket.close();

            Log.getLogger(Application.class).warn("Port (" + port + ") is already in use.");
        } catch (Exception ex) {
            port(port);
        }
    }

    static void reload(boolean log) {
        gameloader.reload(log);
        textureLoader.reload();
    }
}
