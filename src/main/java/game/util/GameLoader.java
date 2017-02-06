package game.util;

import application.Application;
import game.CoordinateMap;
import game.model.Block;
import game.model.Game;
import game.model.Player;
import game.model.event.Event;
import game.model.event.StyleEvent;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created GameLoader.java in game
 * by Arne on 11.01.2017.
 */
public class GameLoader {
    private final Map<String, Game.Builder> gameBuilders = new HashMap<>();
    private final ApplicationProperties properties = new ApplicationProperties();

    private void loadMap(String fileContent, String token) {
        String title = null;
        char playerChar = 'X';
        Point playerPosition = null;
        CoordinateMap.Builder<Block> blocks = new CoordinateMap.Builder<>();
        CoordinateMap.Builder<List<Event>> events = new CoordinateMap.Builder<>();
        Set<String> resources = new HashSet<>();
        GameRenderer.Builder gameRenderer = GameRenderer.Builder.getDefault();

        {
            int width = 0, y = 0, lineNumber = 0;

            String[] lines = fileContent.split("\n");
            for (String line : lines) {
                try {
                    if (line.startsWith("player:")) {
                        playerChar = line.substring("player:".length()).toCharArray()[0];
                    } else if (line.startsWith("title:")) {
                        title = line.substring("title:".length()).replaceAll(" ", "_");
                        title = title.replaceAll("<.*?>", "");
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
                    }
                    lineNumber += 1;
                } catch (Exception e) {
                    throw new RuntimeException("You have a syntax error in line " + (lineNumber + 1) + ": " + e.getMessage(), e);
                }
            }
        }

        if (playerPosition == null) {
            throw new RuntimeException("You have to set the spawn position of the player with the player char (yours: \"" + playerChar + "\").");
        } else if (title == null) {
            throw new RuntimeException("You have to set the title (\"title:example_map\")");
        }

        String key = token == null ? title : token;
        gameBuilders.put(key, new Game.Builder(new Player.Builder(playerChar, playerPosition), blocks, events, gameRenderer, title, resources));
    }

    public Game getInstance(String title) {
        return gameBuilders.get(title).build();
    }

    public void reload(boolean log) {
        gameBuilders.clear();
        getMaps(properties.getVerifiedMapsPath())
                .map(file -> {
                    try {
                        return new Reader(new FileReader(file.getAbsolutePath())).read();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(map -> loadMap(map, null));

        getMaps(properties.getUnverifiedMapsPath())
                .map(file -> {
                    try {
                        HashMap<String, String> tmp = new HashMap<>();
                        tmp.put(new Reader(new FileReader(file.getAbsolutePath())).read(), new File(file.getParent()).getName());
                        return new ArrayList<>(tmp.entrySet()).get(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(entry -> loadMap(entry.getKey(), entry.getValue()));


        if (log) {
            Log.getLogger(getClass()).info("Reloading Maps.");
            gameBuilders.forEach((s, game) -> Log.getLogger(getClass()).info(String.format("Loaded Map \"%s\" with id \"%s\"", s.replaceAll("_", " "), s)));
        }
    }

    public JSONArray loadInstances() {
        JSONArray arr = new JSONArray();
        gameBuilders.keySet().stream()
                .filter(s -> gameBuilders.get(s).getTitle().equals(s))
                .forEach(title -> arr.put(arr.length(), new JSONObject().put("name", title.replace("_", " ")).put("id", title)));
        return arr;
    }

    private Map<String, Game.Builder> getInstances() {
        return gameBuilders;
    }

    public Game.Builder validateMap(String content) {
        loadMap(content, null);
        String title = new ArrayList<>(gameBuilders.entrySet()).get(0).getKey();

        if (Application.gameloader.getInstances().containsKey(title)) {
            throw new RuntimeException("This title is used already.");
        }
        return gameBuilders.get(title);
    }

    private Stream<File> getMaps(Path path) {
        return Utils.listPaths(path).stream()
                .map(mapDir -> mapDir.resolve("map.txt").toFile())
                .filter(File::exists);
    }

    public Game.Builder getBuilder(String key) {
        return gameBuilders.get(key);
    }

    public Map<String, String> listUnverifiedMaps() {
        HashMap<String, String> maps = new HashMap<>();

        gameBuilders.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(entry.getValue().getTitle()))
                .map(Map.Entry::getKey)
                .forEach(mapKey -> maps.put(mapKey, gameBuilders.get(mapKey).getTitle()));

        return maps;
    }
}
