package game.model;

import game.CoordinateMap;
import game.model.event.CheckpointEvent;
import game.model.event.Event;
import game.model.event.StyleEvent;
import game.util.GameRenderer;
import network.Session;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created Game.java in network
 * by Arne on 11.01.2017.
 */
public class Game implements Cloneable {
    private final Set<String> resources = new HashSet<>();
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
    private HashMap<String, String> changedConditions = new HashMap<>();

    private final AtomicInteger failCounter = new AtomicInteger(0);
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    public Game(Player player, Map<Point, Block> blocks, Map<Point, List<Event>> events, GameRenderer gameRenderer, Map<String, String> conditions) {
        this.player = player;
        this.blockMap = new CoordinateMap<>(blocks);
        this.eventMap = new CoordinateMap<>(events);

        this.gameRenderer = gameRenderer;
        this.lastGameRenderer = GameRenderer.getDefault();
        lastGameRenderer.load(gameRenderer);

        this.data = new JSONObject();

        this.conditions.putAll(conditions);

        //region setWidthAndHeight
        int highestX = 0;
        int highestY = 0;

        for (Map.Entry<Point, Block> entry : blocks.entrySet()) {
            Point p = entry.getKey();
            if (p.getX() > highestX) {
                highestX = (int) p.getX();
            }
            if (p.getY() > highestY) {
                highestY = (int) p.getY();
            }
        }

        this.mapWidth = highestX;
        this.mapHeight = highestY;
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
        return getPlayer().isChanged() || !gameRenderer.equals(lastGameRenderer);
    }

    public void incrementTickCounter() {
        tickCounter.incrementAndGet();
    }

    public Statistics buildStatistics() {
        return new Statistics(tickCounter.intValue(), failCounter.intValue());
    }

    public void addAllResources(Collection<? extends String> resourceCollection) {
        resources.addAll(resourceCollection);
    }

    public Set<String> getResources() {
        return resources;
    }

    public Game copy() {
        return new Game(player.copy(), blockMap.copy(), eventMap.copy(), gameRenderer.copy(), new HashMap<>(conditions));
    }
}
