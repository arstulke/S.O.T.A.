package application;

import game.model.Game;
import game.util.ApplicationProperties;
import game.util.FileUtils;
import game.util.GameLoader;
import game.util.Reader;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.jetty.util.log.Log;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created MapUploadRoute in application
 * by ARSTULKE on 25.01.2017.
 */
class MapUploadRoute implements Route {
    private final ApplicationProperties properties = new ApplicationProperties();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/upload");
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        Part file = request.raw().getPart("file");
        ZipInputStream input = new ZipInputStream(file.getInputStream());

        String mapToken = generateToken();
        try {
            unzip(input, mapToken);
            validateMap(mapToken);
            return "Successfully uploaded map";
        } catch (Exception e) {
            File directory = properties.getUnverifiedMapsPath().resolve(mapToken).toFile();
            org.apache.commons.io.FileUtils.deleteDirectory(directory);
            return e.getMessage();
        }
    }

    private String generateToken() {
        int count = 32;
        String token = RandomStringUtils.random(count, true, true);
        while (properties.getUnverifiedMapsPath().resolve(token + "/").toFile().exists()) {
            token = RandomStringUtils.random(count, true, true);
        }

        return token;
    }

    private void validateMap(String mapToken) throws IOException {
        Path path = properties.getUnverifiedMapsPath().resolve(mapToken + "/");

        List<File> textFiles = FileUtils.listFiles(path, (dir, name) -> name.endsWith(".txt"));
        if (textFiles.size() > 1) {
            throw new RuntimeException("Only one .txt file is allowed in the uploaded .zip file.");
        } else if (textFiles.size() == 0) {
            throw new RuntimeException("You have to create a .txt file in the uploaded .zip file.");
        } else {
            File file = textFiles.get(0);
            if (!file.getName().equals("map.txt")) {
                File newFile = new File(file.getParent() + "/map.txt");
                file.renameTo(newFile);
                file = newFile;
            }

            Game.Builder builder = GameLoader.validateMap(new Reader(new FileReader(file)).read());
            Log.getLogger(getClass()).info("A new Map has been successfully uploaded (token: \"" + mapToken + "\", title: \"" + builder.getTitle() + "\").");
            Application.reload(false);
        }
    }

    private void unzip(ZipInputStream input, String mapToken) throws IOException {
        Path path = properties.getUnverifiedMapsPath().resolve(mapToken + "/");
        path.toFile().mkdir();
        ZipEntry entry;
        while ((entry = input.getNextEntry()) != null) {
            if (!entry.getName().contains(".")) {
                new File(path + entry.getName()).mkdir();
            } else {
                File outputFile = path.resolve(entry.getName()).toFile();
                outputFile.createNewFile();
                try (FileOutputStream out = new FileOutputStream(outputFile)) {
                    for (int c = input.read(); c != -1; c = input.read()) {
                        out.write(c);
                    }
                }
            }
        }
    }
}
