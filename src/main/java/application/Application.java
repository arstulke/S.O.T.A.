package application;

import game.util.Reader;
import game.util.GameReader;
import network.WebSocketHandler;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
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
        gameReader.reload();

        Spark.port(80);
        Spark.staticFileLocation("/public");
        Spark.webSocket("/game", WebSocketHandler.class);
        Spark.get("/res", (request, response) -> gameReader.getResources(request.queryMap("map").value()));
        Spark.get("/maps", (request, response) -> {
            response.header("Content-type", "application/json");
            return gameReader.loadInstances().toString();
        });
        Spark.get("/upload", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "<form method=\"post\" action=\"/upload\" enctype=\"multipart/form-data\">\n" +
                        "    <label>Wählen Sie eine Textdatei (*.txt, *.html usw.) von Ihrem Rechner aus.\n" +
                        "        <input name=\"file\" type=\"file\" size=\"50\" accept=\"text/*\">\n" +
                        "    </label>\n" +
                        "    <button>… und ab geht die Post!</button>\n" +
                        "</form>";
            }
        });
        Spark.post("/upload", (request, response) -> {
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/upload");
            request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            Part file = request.raw().getPart("file");
            String content = new Reader(new InputStreamReader(file.getInputStream())).read();

            GameReader reader = new GameReader();
            try {
                boolean result = reader.saveMap(content);
                gameReader.reload();
                return result ? "Successfully uploaded map" : "Unknown Error";
            } catch (Exception e) {
                return e.getMessage();
            }
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
