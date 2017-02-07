package application;

import game.util.ApplicationProperties;
import game.util.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created TextureLoader in application
 * by ARSTULKE on 26.01.2017.
 */
class TextureLoader {
    private final Map<String, Map<String, BufferedImage>> textures = new HashMap<>();
    private final ApplicationProperties properties = new ApplicationProperties();

    BufferedImage getTexture(String map, String name) {
        if (!textures.containsKey(map) || !textures.get(map).containsKey(name)) {
            return null;
        }
        return textures.get(map).get(name);
    }

    void reload() {
        textures.clear();

        Set<File> mapDirectories = new HashSet<>();
        mapDirectories.addAll(Utils.listFiles(properties.getVerifiedMapsPath()));
        mapDirectories.addAll(Utils.listFiles(properties.getUnverifiedMapsPath()));

        mapDirectories.forEach(this::loadMap);
    }

    private void loadMap(File mapDirectory) {
        String mapKey = mapDirectory.getName();

        if (mapDirectory.toPath().resolve("textures").toFile().exists()) {
            Set<File> textures = new HashSet<>();
            textures.addAll(Utils.listFiles(mapDirectory.toPath().resolve("textures"), (dir, name) -> name.endsWith(".png")));

            textures.forEach(textureFile -> loadTexture(mapKey, textureFile));
        } else {
            textures.put(mapKey, null);
            Application.gameloader.getBuilder(mapKey).setToEditorMode();
        }
    }

    private void loadTexture(String mapKey, File textureFile) {
        BufferedImage img = loadImage(textureFile);
        String name = textureFile.getName().split("\\.")[0];
        if (!textures.containsKey(mapKey)) {
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
