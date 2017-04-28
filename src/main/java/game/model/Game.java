package game.model;

import game.CoordinateMap;
import game.event.CheckpointEvent;
import game.event.Event;
import game.event.StyleEvent;
import game.util.GameRenderer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import network.Session;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created Game.java in network
 * by Arne on 11.01.2017.
 */
public class Game implements Cloneable {

    private final CoordinateMap<Block> blockMap;
    private final CoordinateMap<List<Event>> eventMap;
    private final Player player;

    private final GameRenderer gameRenderer;
    private final GameRenderer lastGameRenderer;

    private final JSONObject data;

    private final int mapWidth;
    private final int mapHeight;

    private final Set<Event> executedEvents = new HashSet<>();
    private final CoordinateMap<Block> changedBlocks = new CoordinateMap<>();

    private final HashMap<String, String> conditions = new HashMap<>();
    private final HashMap<String, String> changedConditions = new HashMap<>();

    private final AtomicInteger failCounter = new AtomicInteger(0);
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    private Game(Player player, CoordinateMap<Block> blocks, CoordinateMap<List<Event>> events, GameRenderer gameRenderer) {
        this.player = player;
        this.blockMap = blocks;
        this.eventMap = events;

        this.gameRenderer = gameRenderer;
        this.lastGameRenderer = gameRenderer;

        this.data = new JSONObject();

        //region setWidthAndHeight
        IntegerProperty highestX = new SimpleIntegerProperty(0);
        IntegerProperty highestY = new SimpleIntegerProperty(0);

        blocks.forEach((p, block) -> {
            if (p.getX() > highestX.get()) {
                highestX.set((int) p.getX());
            }
            if (p.getY() > highestY.get()) {
                highestY.set((int) p.getY());
            }
        });

        this.mapWidth = highestX.get();
        this.mapHeight = highestY.get();
        //endregion
    }

    public JSONObject getData() {
        return data;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public Block getBlockAbove() {
        return getBlock(0, -1);
    }

    public Block getBlockBelow() {
        return getBlock(0, 1);
    }

    public Block getBlock(int x, int y) {
        Point playerPosition = player.getPosition();
        return getBlock(new Point(
                (int) playerPosition.getX() + x,
                (int) playerPosition.getY() + y)
        );
    }

    public void setBlock(Point point, Block block) {
        if (!changedBlocks.containsKey(point)) {
            changedBlocks.set(point, blockMap.get(point));
        }
        blockMap.set(point, block);
    }

    public Block getBlock(Point point) {
        return blockMap.get(point);
    }

    public List<Event> getEvents(Point point) {
        return eventMap.get(point);
    }

    public String getCondition(String conditionName) {
        return conditions.get(conditionName);
    }

    public void setCondition(String name, String value) {
        changedConditions.put(name, conditions.get(name));
        conditions.put(name, value);
    }

    public void respawn(Session session) {
        player.setPosition(player.getSpawnPoint());
        Set<Event> executedEvents = this.executedEvents.stream().filter(event -> event instanceof CheckpointEvent).collect(Collectors.toSet());
        this.executedEvents.clear();
        this.executedEvents.addAll(executedEvents);
        this.gameRenderer.load(this.lastGameRenderer);
        this.changedBlocks.forEach(this.blockMap::set);
        this.changedConditions.forEach(this.conditions::put);

        this.failCounter.incrementAndGet();


        session.sendMessage(
                new JSONObject()
                        .put("cmd", "CLEAR-MESSAGES")
        );
    }

    public void checkpoint(Point target) {
        player.setSpawnPoint(target);
        lastGameRenderer.load(gameRenderer);

        changedBlocks.clear();
        changedConditions.clear();
    }

    public boolean isPlayerOnSolidGround() {
        Point playerPosition = player.getPosition();

        Block below = getBlock(new Point((int) playerPosition.getX(), (int) playerPosition.getY() + 1));
        Block actual = getBlock(playerPosition);
        return below == null || below.isSolid() || below.hasSolidTop() || actual.hasSolidGround();
    }

    public boolean isJumping() {
        return data.has("jumping") && data.getBoolean("jumping");
    }

    public boolean isAutoJumping() {
        return data.has("autoJumping") && data.getBoolean("autoJumping");
    }

    public Block getActualBlock() {
        return getBlock(player.getPosition());
    }

    public boolean isEventExecuted(Event event) {
        return executedEvents.contains(event);
    }

    public void executedEvent(Event event) {
        if (!(event instanceof StyleEvent)) {
            executedEvents.add(event);
        }
    }

    public boolean isChanged() {
        return getPlayer().isNotOnSpawnPoint() || !gameRenderer.equals(lastGameRenderer);
    }

    public void incrementTickCounter() {
        tickCounter.incrementAndGet();
    }

    public Statistics buildStatistics() {
        return new Statistics(tickCounter.intValue(), failCounter.intValue());
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static class Builder {
        private final String title;
        private final String token;
        private final Player.Builder playerBuilder;
        private final CoordinateMap.Builder<Block> blocks;
        private final CoordinateMap.Builder<List<Event>> events;
        private final GameRenderer.Builder gameRenderer;
        private final Set<String> resources;
        private final boolean verified;
        private boolean textures = true;

        public Builder(Player.Builder playerBuilder, CoordinateMap.Builder<Block> blocks, CoordinateMap.Builder<List<Event>> events, GameRenderer.Builder gameRenderer, String title, String token, Set<String> resources) {
            this.playerBuilder = playerBuilder;
            this.blocks = blocks;
            this.events = events;
            this.gameRenderer = gameRenderer;
            this.title = title;
            this.token = token;
            this.verified = title.equals(token);
            this.resources = resources;
        }

        public String getTitle() {
            return title;
        }

        public Game build() {
            return new Game(playerBuilder.build(), blocks.build(), events.build(), gameRenderer.build());
        }

        public void setToEditorMode() {
            this.textures = true;
        }

        public List<String> getResources(String mode) {
            List<String> resources = new ArrayList<>(this.resources);
            resources.addAll(gameRenderer.getResources());
            if (mode != null && mode.equals("textures")) {
                if (textures) {
                    resources.addAll(getTextures());
                    resources.add("/textures?map=" + token + "&name=stick");
                    resources.add("/textures?map=" + token + "&name=minus");
                }
                resources.add(0, "/error.png");
            }
            return resources;
        }

        private Set<String> getTextures() {
            Function<? super Character, String> function = character -> {
                String ch = character + "";
                if (Character.isLowerCase(ch.charAt(0))) {
                    ch = "k" + ch;
                } else {
                    ch = ch.toLowerCase();
                }

                ch = ch
                        .replace(",", "comma")
                        .replace(".", "dot")
                        .replace("/", "slash")
                        .replace("\\", "backslash")
                        .replace(":", "double")
                        .replace("*", "star")
                        .replace("?", "questionmark")
                        .replace("\"", "syno")
                        .replace("<", "smaller")
                        .replace(">", "bigger")
                        .replace("|", "stick")
                        .replace(" ", "space")
                        .replace("^", "spike")
                        .replace("#", "hashtag")
                        .replace("_", "k_")
                        .replace("+", "plus")
                        .replace("-", "minus");

                return "/textures?map=" + token + "&name=" + ch;
            };

            Set<String> textures = blocks.stream()
                    .map(Map.Entry::getValue)
                    .map(Block::getChar)
                    .distinct()
                    .map(function)
                    .collect(Collectors.toSet());
            textures.add(function.apply(playerBuilder.getPlayerChar()));

            return textures;
        }

        public String getKey() {
            return verified ? title : token;
        }
    }
}
