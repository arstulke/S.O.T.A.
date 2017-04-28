package de.aska.game.model;

import java.awt.*;

/**
 * Created Player.java in game.model
 * by Arne on 11.01.2017.
 */
public class Player {
    private final char playerChar;
    private final Point playerPosition;
    private final Point lastTickPosition;
    private final Point spawnPoint;
    private final Keys keys;

    private Player(char playerChar, Point playerPosition) {
        this.playerChar = playerChar;
        this.playerPosition = new Point();
        this.playerPosition.setLocation(playerPosition);

        this.spawnPoint = new Point();
        this.spawnPoint.setLocation(playerPosition);

        this.lastTickPosition = new Point();
        this.lastTickPosition.setLocation(playerPosition);

        this.keys = new Keys();
    }

    public char getPlayerChar() {
        return playerChar;
    }

    public Point getPosition() {
        return playerPosition;
    }

    public Point getSpawnPoint() {
        return spawnPoint;
    }

    public Keys getKeys() {
        return keys;
    }

    public void setPosition(Point playerPosition) {
        this.playerPosition.setLocation(playerPosition);
    }

    public void setSpawnPoint(Point spawnPoint) {
        this.spawnPoint.setLocation(spawnPoint);
    }

    public void setKeys(String key) {
        this.keys.setKey(key);
    }

    public boolean isNotOnSpawnPoint() {
        return !playerPosition.equals(spawnPoint);
    }

    public boolean hasMoved() {
        return !lastTickPosition.equals(playerPosition);
    }

    public void savePosition() {
        this.lastTickPosition.setLocation(this.playerPosition);
    }

    public static class Builder {
        private final char playerChar;
        private final Point playerPosition;

        public Builder(char playerChar, Point playerPosition) {
            this.playerChar = playerChar;
            this.playerPosition = playerPosition;
        }

        public Player build() {
            return new Player(playerChar, playerPosition);
        }

        char getPlayerChar() {
            return playerChar;
        }
    }
}
