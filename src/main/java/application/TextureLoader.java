package application;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created TextureLoader in application
 * by ARSTULKE on 26.01.2017.
 */
public class TextureLoader {
    private Map<String, Map<String, BufferedImage>> textures = new HashMap<>();

    public BufferedImage getTexture(String map, String name) {
        return textures.get(map).get(name);
    }

    public void reload() {
        textures.clear();

        Set<File> mapDirectories = new HashSet<>();
        mapDirectories.addAll(Arrays.asList(new File(System.getProperty("user.dir") + "/maps").listFiles()));
        mapDirectories.addAll(Arrays.asList(new File(System.getProperty("user.dir") + "/unverifiedMaps").listFiles()));

        mapDirectories.forEach(mapDirectory -> loadMap(mapDirectory));
    }

    private void loadMap(File mapDirectory) {
        String mapKey = mapDirectory.getName();

        Set<File> textures = new HashSet<>();
        textures.addAll(Arrays.asList(mapDirectory.toPath().resolve("textures").toFile().listFiles((dir, name) -> name.endsWith(".png"))));

        textures.forEach(textureFile -> loadTexture(mapKey, textureFile));
    }

    private void loadTexture(String mapKey, File textureFile) {
        BufferedImage img = loadImage(textureFile);
        String name = textureFile.getName().split("\\.")[0];
        if(!textures.containsKey(mapKey)) {
            textures.put(mapKey, new HashMap<>());
        }
        textures.get(mapKey).put(name, img);
    }

    private BufferedImage loadImage(File textureFile) {
        try {
            return ImageIO.read(textureFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
