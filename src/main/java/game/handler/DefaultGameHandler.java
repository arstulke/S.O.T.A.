package game.handler;


import game.model.Game;
import game.model.Keys;
import game.model.block.Block;
import game.util.Properties;
import network.Session;
import org.json.JSONObject;

import java.awt.*;

/**
 * Created DefaultGameHandler.java in game
 * by Arne on 11.01.2017.
 */
public class DefaultGameHandler implements GameHandler {

    private Properties properties;

    private boolean updated;
    private Session session;
    private Game game;
    private Keys keys;

    private Block above;
    private Block below;
    private Block actual;


    public DefaultGameHandler() {
        this.properties = new Properties();
    }

    @Override
    public boolean update(Session session, Game game) {
        updated = false;
        this.session = session;
        this.game = game;
        keys = game.getPlayer().getKeys();
        above = game.getBlockAbove();
        below = game.getBlockBelow();
        actual = game.getActualBlock();

        boolean respawn = checkDie() || game.getPlayer().getKeys().respawn();
        boolean isPlayerOnSpawnPoint = game.getPlayer().isOnSpawnPoint();
        if (respawn && !isPlayerOnSpawnPoint) {
            respawn(session, game);
        }

        try {
            physics();
            events();
        } catch (NullPointerException ignore) {
        } catch (RuntimeException e) {
            if (!"You died.".equals(e.getMessage())) {
                throw e;
            }
        }

        return updated;
    }

    private void respawn(Session session, Game game) {
        game.respawn(session);
        updated = true;

        above = game.getBlockAbove();
        below = game.getBlockBelow();
        actual = game.getActualBlock();
    }

    private void events() {
        game.getEvents(game.getPlayer().getPosition()).forEach(event -> {
            if (!game.isEventExecuted(event)) {
                event.execute(session, game);
                game.executedEvent(event);
            }
        });
    }

    private void physics() {
        //region gravity
        if (!game.isPlayerOnSolidGround() && !game.isJumping() && !game.isAutoJumping()) {
            move(0, 1);
        }
        //endregion

        //region Jump
        if (keys.up() && !keys.down() && game.isPlayerOnSolidGround() && !actual.hasSolidTop() && !game.isAutoJumping() && !game.isJumping()) {
            beginJump();
        }

        if (game.isJumping()) {
            int tick = game.getData().getJSONObject("jump").getInt("tick");
            if (tick < 2 && tick >= 0) {
                move(0, -1);
            } else if (tick < 5 && tick >= 3) {
                if (!below.hasSolidTop() && !actual.hasSolidGround()) {
                    move(0, 1);
                }
            }

            if (tick == 5) {
                game.getData().put("jumping", false);
            }
            game.getData().getJSONObject("jump").put("tick", tick + 1);
        }
        //endregion

        //region AutoJump
        if (properties.isAutoJumpEnabled()) {
            if ((keys.onlyLeft() || keys.onlyRight()) && !keys.up() && !keys.down() && !game.isJumping() && !game.isAutoJumping() && game.isPlayerOnSolidGround()) {
                Block blockToMove = game.getBlock(keys.onlyLeft() ? -1 : 1, 0);
                Block aboveBlockToMove = game.getBlock(keys.onlyLeft() ? -1 : 1, -1);
                if (blockToMove.isSolid() && !aboveBlockToMove.isSolid()) {
                    beginAutoJump();
                }
            }

            if (game.isAutoJumping()) {
                int tick = game.getData().getJSONObject("autoJump").getInt("tick");

                if (tick == 1) {
                    move(0, -1);
                } else if (tick == 2) {
                    move(game.getData().getJSONObject("autoJump").getInt("direction"), 0);
                }

                if (tick == 2) {
                    game.getData().put("autoJumping", false);
                }
                game.getData().getJSONObject("autoJump").put("tick", tick + 1);
            }
        }
        //endregion

        //region climb Ladder
        if (keys.up() && !keys.down() && (above.hasSolidGround() || actual.hasSolidTop()) && game.isPlayerOnSolidGround()) {
            move(0, -1);
        } else if (!keys.up() && keys.down()) {
            move(0, 1);
        }
        //endregion

        //region left/right
        if (keys.onlyRight()) {
            move(1, 0);
        } else if (keys.onlyLeft()) {
            move(-1, 0);
        }
        //endregion
    }

    private void beginAutoJump() {
        JSONObject data = game.getData();
        JSONObject autoJumpData = new JSONObject();

        int dir = keys.onlyLeft() ? -1 : 1;
        autoJumpData.put("direction", dir);
        autoJumpData.put("tick", 0);

        data.put("autoJump", autoJumpData);
        data.put("autoJumping", true);
    }

    private void beginJump() {
        JSONObject data = game.getData();
        JSONObject jumpData = new JSONObject();

        jumpData.put("tick", 0);

        data.put("jump", jumpData);
        data.put("jumping", true);
    }

    private void move(int x, int y) {
        Point playerPosition = game.getPlayer().getPosition();

        Point positionToMove = new Point((int) (playerPosition.getX() + x), (int) (playerPosition.getY() + y));
        Block blockToMove = game.getBlock(positionToMove);
        if (blockToMove != null) {
            if (!blockToMove.isSolid()) {
                int slowDownTicks;
                if (game.getData().has("slowDownTicks")) {
                    slowDownTicks = game.getData().getInt("slowDownTicks");
                } else {
                    slowDownTicks = 1;
                }

                if (blockToMove.getSlowDown() <= slowDownTicks) {
                    game.getPlayer().setPosition(positionToMove);
                    updated = true;


                    above = game.getBlockAbove();
                    below = game.getBlockBelow();
                    actual = game.getActualBlock();
                    if (checkDie()) {
                        throw new RuntimeException("You died.");
                    }

                    game.getData().put("slowDownTicks", 1);
                } else {
                    game.getData().put("slowDownTicks", slowDownTicks + 1);
                }
            } else if (blockToMove.getChar() == '^' && y == 1) {
                respawn(session, game);
            }
        }
    }

    private boolean checkDie() {
        Block below = game.getBlockBelow();
        Block actual = game.getActualBlock();
        return below != null && below.getChar() == '^' && !actual.hasSolidGround();
    }
}
