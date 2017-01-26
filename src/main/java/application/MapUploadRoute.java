package application;

import game.util.GameBuilder;
import game.util.GameLoader;
import game.util.Reader;
import org.apache.commons.io.FileUtils;
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
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created MapUploadRoute in application
 * by ARSTULKE on 25.01.2017.
 */
public class MapUploadRoute implements Route {
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
            File directory = new File(System.getProperty("user.dir") + "/unverifiedMaps/" + mapToken);
            FileUtils.deleteDirectory(directory);
            return e.getMessage();
        }
    }

    private String generateToken() {
        int count = 32;
        String token = RandomStringUtils.random(count, true, true);
        while (Paths.get(System.getProperty("user.dir") + "/unverifiedMaps/" + token + "/").toFile().exists()) {
            token = RandomStringUtils.random(count, true, true);
        }

        return token;
    }

    private void validateMap(String mapToken) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir") + "/unverifiedMaps/" + mapToken + "/");

        if (!path.resolve("textures").toFile().exists()) {
            throw new RuntimeException("You have to create the \"textures\" directory in the uploaded .zip file.");
        }

        List<File> textFiles = Arrays.asList(path.toFile().listFiles((dir, name) -> name.endsWith(".txt")));
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

            GameLoader reader = new GameLoader();
            GameBuilder builder = reader.validateMap(new Reader(new FileReader(file)).read());

            Log.getLogger(getClass()).info("A new Map has been successfully uploaded (token: \"" + mapToken + "\", title: \"" + builder.getTitle() + "\").");
            Application.gameloader.reload(false, true);
            Application.textureLoader.reload();
        }
    }

    /*
    *  else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/maps/" + title + ".txt"));
            writer.write(content);
            writer.close();
        }
    * */

    private void unzip(ZipInputStream input, String mapToken) throws IOException {
        String path = System.getProperty("user.dir") + "/unverifiedMaps/" + mapToken + "/";
        new File(path).mkdir();
        ZipEntry entry;
        while ((entry = input.getNextEntry()) != null) {
            File outputFile = new File(path + entry.getName());
            if (!entry.getName().contains(".")) {
                new File(path + entry.getName()).mkdir();
            } else {
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
