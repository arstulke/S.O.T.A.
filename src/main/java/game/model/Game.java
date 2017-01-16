package game.model;

import game.CoordinateMap;
import game.model.event.CheckpointEvent;
import game.model.event.Event;
import game.util.GameRenderer;
import network.Session;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created Game.java in network
 * by Arne on 11.01.2017.
 */
public class Game implements Cloneable {
    private final CoordinateMap<Block> blockMap;
    private final CoordinateMap<List<Event>> eventMap;
    private final Set<Event> executedEvents = new HashSet<>();
    private final Player player;

    private final GameRenderer gameRenderer;
    private final GameRenderer lastGameRenderer;

    private final Point originSpawn;
    private final JSONObject data;

    private final int mapWidth;
    private final int mapHeight;

    private final AtomicInteger failCounter = new AtomicInteger(0);
    private final AtomicInteger tickCounter = new AtomicInteger(0);

    public Game(Player player, Map<Point, Block> blocks, Map<Point, List<Event>> events, GameRenderer gameRenderer) {
        this.player = player;
        this.blockMap = new CoordinateMap<>(blocks);
        this.eventMap = new CoordinateMap<>(events);

        this.gameRenderer = gameRenderer;
        this.lastGameRenderer = GameRenderer.getDefault();
        lastGameRenderer.load(gameRenderer);

        this.originSpawn = new Point();
        this.originSpawn.setLocation(player.getSpawnPoint());

        this.data = new JSONObject();

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

    public void saveGameRenderer() {
        lastGameRenderer.load(gameRenderer);
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

    public Block getBlock(Point point) {
        return blockMap.get(point);
    }

    public List<Event> getEvents(Point point) {
        return eventMap.get(point);
    }

    public void respawn(Session session) {
        player.setPosition(player.getSpawnPoint());
        Set<Event> executedEvents = this.executedEvents.stream().filter(event -> event instanceof CheckpointEvent).collect(Collectors.toSet());
        this.executedEvents.clear();
        this.executedEvents.addAll(executedEvents);
        this.gameRenderer.load(this.lastGameRenderer);

        this.failCounter.incrementAndGet();

        session.sendMessage(
                new JSONObject()
                        .put("cmd", "CLEAR-MESSAGES")
        );
    }

    public Game copy() {
        return new Game(player.copy(), blockMap.copy(), eventMap.copy(), gameRenderer.copy());
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
        executedEvents.add(event);
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
}
