package game.model.event;

import game.model.Game;
import game.util.GameRenderer;
import network.Session;

import java.awt.*;

/**
 * Created StyleEvent.java in game.model.event
 * by Arne on 13.01.2017.
 */
public class StyleEvent extends Event {
    private final String backgroundValue;
    private final String foregroundValue;

    public StyleEvent(Rectangle triggerArea, String backgroundValue, String foregroundValue) {
        super(triggerArea);
        this.backgroundValue = backgroundValue;
        this.foregroundValue = foregroundValue;
    }

    @Override
    public void execute(Session session, Game game) {
        GameRenderer gameRenderer = game.getGameRenderer();
        if(this.backgroundValue != null) {
            gameRenderer.setBackgroundColor(this.backgroundValue);
        }
        if(this.foregroundValue != null) {
            gameRenderer.setForegroundColor(this.foregroundValue);
        }
    }
}
