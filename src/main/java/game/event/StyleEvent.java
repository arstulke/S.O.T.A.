package game.event;

import game.model.Game;
import game.util.GameRenderer;
import network.Session;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created StyleEvent.java in event.event
 * by Arne on 13.01.2017.
 */
public class StyleEvent extends Event {
    private final String backgroundValue;
    private final String foregroundValue;

    public StyleEvent(Rectangle triggerArea, String backgroundValue, String foregroundValue) {
        super(triggerArea, true);
        this.backgroundValue = backgroundValue;
        this.foregroundValue = foregroundValue;
    }

    @Override
    public void execute(Session session, Game game) {
        GameRenderer gameRenderer = game.getGameRenderer();
        if (this.backgroundValue != null) {
            gameRenderer.setBackgroundColor(this.backgroundValue);
        }
        if (this.foregroundValue != null) {
            gameRenderer.setForegroundColor(this.foregroundValue);
        }
    }

    public Set<String> getResources() {
        Set<String> res = new HashSet<>();
        if (this.backgroundValue.startsWith("http")) {
            res.add(backgroundValue);
        }
        if (this.foregroundValue.startsWith("http")) {
            res.add(foregroundValue);
        }
        return res;
    }
}
