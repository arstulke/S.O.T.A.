package game.util;

import java.io.IOException;

/**
 * Created Properties.java in game.util
 * by Arne on 12.01.2017.
 */
public class Properties {
    private final java.util.Properties properties;

    public Properties() {
        try {
            java.util.Properties properties = new java.util.Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            this.properties = properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAutoJumpEnabled() {
        return !properties.containsKey("auto-jump") || properties.getProperty("auto-jump").equals("true");
    }

    public String getMapFilename() {
        return properties.getProperty("map-file");
    }
}
