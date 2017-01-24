package game.util;

import game.model.Block;
import game.model.Game;
import game.model.Player;
import game.model.event.Event;
import game.model.event.StyleEvent;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created GameReader.java in game
 * by Arne on 11.01.2017.
 */
public class GameReader {
    private final Map<String, Game> gameInstances = new HashMap<>();

    public GameReader() throws IOException {
        reload();
    }

    private void loadMap(String mapFilename) throws IOException {
        String fileContent = getFileContent(mapFilename);

        char playerChar = 'X';
        String title = "";
        Point playerPosition = null;
        Map<Point, Block> blocks = new HashMap<>();
        Map<Point, List<Event>> events = new HashMap<>();
        Map<String, String> conditions = new HashMap<>();
        GameRenderer gameRenderer = GameRenderer.getDefault();
        Set<String> resources = new HashSet<>();

        {
            int width = 0, y = 0, lineNumber = 0;

            String[] lines = fileContent.split("\n");
            for (String line : lines) {
                try {
                    if (line.startsWith("player:")) {
                        playerChar = line.substring("player:".length()).toCharArray()[0];
                    } else if (line.startsWith("title:")) {
                        title = line.substring("title:".length()).replaceAll(" ", "_");
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
                            if (event instanceof StyleEvent) {
                                resources.addAll(((StyleEvent) event).getResources());
                            }
                        }
                    } else if (line.startsWith("background:")) {
                        gameRenderer.setBackgroundColor(line.substring("background:".length()));
                    } else if (line.startsWith("foreground:")) {
                        gameRenderer.setForegroundColor(line.substring("foreground:".length()));
                    } else if (line.startsWith("* condition")) {
                        Map.Entry<String, String> condition = EventBuilder.buildCondition(line);
                        conditions.put(condition.getKey(), condition.getValue());
                    }
                    lineNumber += 1;
                } catch (Exception e) {
                    throw new RuntimeException("You have a syntax error in line " + (lineNumber + 1) + ": " + e.getMessage(), e);
                }
            }
        }

        if (playerPosition == null) {
            throw new RuntimeException("You have to set the spawn position of the player with the player char (yours: \"" + playerChar + "\").");
        }

        gameInstances.put(title, new Game(new Player(playerChar, playerPosition), blocks, events, gameRenderer, conditions));
        gameInstances.get(title).addAllResources(resources);
    }

    private String getFileContent(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String content = "", line;
            while ((line = br.readLine()) != null) {
                content += line + "\n";
            }
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    public Game getInstance(String title) {
        return gameInstances.get(title).copy();
    }

    public Set<String> getResources(String mapname) {
        return gameInstances.get(mapname).getResources();
    }

    public void reload() throws IOException {
        File mapDirectory = new File(System.getProperty("user.dir") + "/maps/");
        if (mapDirectory.exists()) {
            File[] maps = mapDirectory.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".txt"));

            if (maps != null) {
                for (File map : maps) {
                    loadMap(map.getAbsolutePath());
                }
            }
        } else {
            mapDirectory.mkdir();
        }
    }
}
