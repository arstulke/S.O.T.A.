package game.util;

import game.model.Game;
import game.model.Player;
import game.model.block.Block;
import game.model.event.Event;
import game.util.event.EventBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * Created GameReader.java in game
 * by Arne on 11.01.2017.
 */
public class GameReader {
    private final Game instance;

    public GameReader() {
        try {
            Properties prop = new Properties();
            String fileContent = getFileContent(prop.getMapFilename());

            char playerChar = 'X';
            Point playerPosition = null;
            Map<Point, Block> blocks = new HashMap<>();
            Map<Point, List<Event>> events = new HashMap<>();

            {
                int width = 0;
                int y = 0;

                String[] lines = fileContent.split("\n");
                for (String line : lines) {
                    if (line.startsWith("player:")) {
                        playerChar = line.substring("player:".length()).toCharArray()[0];
                    } else if (line.startsWith("-")) {
                        width = line.length() - 1;
                    } else if (line.startsWith(":")) {
                        line = line.substring(1, Math.min(width + 1, line.length()));
                        line = line.replaceAll("\t", " ");
                        for (int x = 0; x < line.length(); x++) {
                            Point p = new Point(x, y);
                            if (line.charAt(x) == playerChar) {
                                playerPosition = new Point();
                                playerPosition.setLocation(p);
                                blocks.put(p, Block.build(' '));
                            } else {
                                blocks.put(p, Block.build(line.charAt(x)));
                            }
                        }
                        if (line.length() < width) {
                            for (int x = line.length(); x < width; x++) {
                                Point p = new Point(x, y);
                                blocks.put(p, Block.build(' '));
                            }
                        }

                        y += 1;
                    } else if (line.startsWith("~")) {
                        Event event = EventBuilder.buildEvent(line);
                        if (event != null) {
                            event.getTriggerPoints().forEach(point -> {
                                if (!events.containsKey(point)) {
                                    events.put(point, new ArrayList<>(Collections.singletonList(event)));
                                } else if (!events.get(point).contains(event)) {
                                    events.get(point).add(event);
                                }
                            });
                        }
                    }
                }
            }

            if (playerPosition == null) {
                throw new RuntimeException("You have to set the spawn position of the player with the player char (yours: \"" + playerChar + "\").");
            }

            instance = new Game(new Player(playerChar, playerPosition), blocks, events);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileContent(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new RuntimeException("Die Datei \"" + fileName + "\" existiert nicht.");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String content = "", line;
        while ((line = br.readLine()) != null) {
            content += line + "\n";
        }
        return content;
    }

    public Game getInstance() {
        return instance.copy();
    }
}
