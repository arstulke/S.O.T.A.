package game.model;

import game.model.block.Block;
import game.model.event.Event;
import game.util.GameRenderer;
import network.Session;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created Game.java in network
 * by Arne on 11.01.2017.
 */
public class Game implements Cloneable {
    private final Map<Point, Block> blocks;
    private final Map<Point, List<Event>> events;
    private final Set<Event> executedEvents = new HashSet<>();
    private final Player player;
    private final GameRenderer gameRenderer = GameRenderer.DEFAULT;
    private final Point originSpawn;
    private final JSONObject data;
    private final int width;
    private final int height;

    public Game(Player player, Map<Point, Block> blocks, Map<Point, List<Event>> events) {
        this.player = player;
        this.blocks = blocks;
        this.events = events;

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

        this.width = highestX;
        this.height = highestY;
        //endregion
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
        return blocks.get(new Point(
                (int) playerPosition.getX() + x,
                (int) playerPosition.getY() + y)
        );
    }

    public Block getBlock(Point point) {
        return blocks.get(point);
    }

    public List<Event> getEvents(Point point) {
        return events.get(point);
    }

    public JSONObject getData() {
        return data;
    }

    public Player getPlayer() {
        return player;
    }

    public Point getOriginSpawn() {
        return originSpawn;
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

    public void respawn(Session session) {
        player.setPosition(player.getSpawnPoint());
        executedEvents.clear();

        session.sendMessage(
                new JSONObject()
                        .put("cmd", "CLEAR-MESSAGES")
        );
    }

    public Game copy() {
        return new Game(player.copy(), new HashMap<>(blocks), new HashMap<>(events));
    }

    public boolean isPlayerAtSpawnPoint() {
        return player.getPosition().equals(player.getSpawnPoint());
    }

    public boolean isEventExecuted(Event event) {
        return executedEvents.contains(event);
    }

    public void executedEvent(Event event) {
        executedEvents.add(event);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
