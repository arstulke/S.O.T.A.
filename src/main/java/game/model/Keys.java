package game.model;

import java.util.HashMap;

/**
 * Created Keys.java in game.model
 * by Arne on 11.01.2017.
 */
public class Keys {
    private final HashMap<String, Boolean> keys = new HashMap<>();

    public void setKey(String key) {
        if (keys.containsKey(key)) {
            keys.put(key, !keys.get(key));
        } else {
            keys.put(key, true);
        }
    }

    public boolean right() {
        return key("right");
    }

    public boolean left() {
        return key("left");
    }

    public boolean up() {
        return key("up");
    }

    public boolean down() {
        return key("down");
    }

    private boolean key(String key) {
        return keys.containsKey(key) && keys.get(key);
    }

    public boolean onlyRight() {
        return right() && !left();
    }

    public boolean onlyLeft() {
        return !right() && left();
    }

    public boolean respawn() {
        boolean value = key("respawn");
        if (value) {
            keys.put("respawn", false);
        }
        return value;
    }
}
