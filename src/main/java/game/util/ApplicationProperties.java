package game.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created ApplicationProperties.java in game.util
 * by Arne on 12.01.2017.
 */
public class ApplicationProperties {
    private static Properties properties;

    public ApplicationProperties() {
        try {
            if (properties == null) {
                Properties properties = new Properties();
                properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
                ApplicationProperties.properties = properties;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getUnverifiedMapsPath() {
        String name = "unverifiedMaps";
        if (properties.containsKey(name)) {
            name = properties.getProperty(name);
        }

        return Paths.get(System.getProperty("user.dir")).resolve(name);
    }

    public Path getVerifiedMapsPath() {
        String name = "maps";
        if (properties.containsKey("verifiedMaps")) {
            name = properties.getProperty("verifiedMaps");
        }

        return Paths.get(System.getProperty("user.dir")).resolve(name);
    }

    public int getPort() {
        return properties.containsKey("port") ? Integer.parseInt(properties.getProperty("port")) : 80;
    }
}
