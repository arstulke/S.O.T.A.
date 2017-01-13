package network;

import game.handler.DefaultGameHandler;
import game.handler.GameHandler;
import game.model.Game;
import game.util.GameReader;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created network.WebSocketHandler.java in PACKAGE_NAME
 * by Arne on 11.01.2017.
 */
@WebSocket
public class WebSocketHandler {
    private final Map<Session, Game> games = new HashMap<>();

    private GameHandler gameHandler;
    private final GameReader gameReader = new GameReader();
    private final Timer timer = new Timer();

    @OnWebSocketClose
    public void onDisconnect(org.eclipse.jetty.websocket.api.Session webSocketSession, int statusCode, String reason) {
        games.remove(new Session(webSocketSession));
    }

    @OnWebSocketConnect
    public void onConnect(org.eclipse.jetty.websocket.api.Session webSocketSession) {
        initialize();

        Session session = new Session(webSocketSession);
        if (!games.containsKey(session)) {
            games.put(session, gameReader.getInstance());
            onConnect(session.getSession());
        } else {
            Game initGame = games.get(session);
            session.sendMessage(new JSONObject()
                    .put("cmd", "OUTPUT")
                    .put("msg", initGame.getGameRenderer().render(initGame))
                    .put("position", toJSON(initGame.getPlayer().getPosition()))
            );
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Game game = games.get(session);
                    if (game != null) {
                        boolean update = gameHandler.update(session, game);
                        if (update) {
                            String output = game.getGameRenderer().render(game);
                            session.sendMessage(
                                    new JSONObject()
                                            .put("cmd", "OUTPUT")
                                            .put("msg", output)
                                            .put("position", toJSON(game.getPlayer().getPosition()))
                            );
                        } else {
                            session.sendMessage(
                                    new JSONObject()
                                            .put("cmd", "PING-OUTPUT")
                            );
                        }
                    } else {
                        this.cancel();
                    }
                }
            };
            try {
                timer.schedule(task, 0, 100);
            } catch (Exception ignored) {
            }
        }
    }

    private JSONObject toJSON(Object object) {
        if (object instanceof Point) {
            Point point = (Point) object;
            return new JSONObject()
                    .put("x", (int) point.getX())
                    .put("y", (int) point.getY());
        } else {
            return null;
        }
    }

    @OnWebSocketMessage
    public void onMessage(org.eclipse.jetty.websocket.api.Session webSocketSession, String message) {
        Session session = new Session(webSocketSession);
        if (games.containsKey(session)) {
            Game game = games.get(session);
            game.getPlayer().setKeys(message);
        }
    }

    private void initialize() {
        if (gameHandler == null) {
            gameHandler = new DefaultGameHandler();
        }
    }
}
