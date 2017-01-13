package game.model.event;

import game.model.Game;
import network.Session;

import java.awt.*;

/**
 * Created DummyEvent.java in game.model.event
 * by Arne on 12.01.2017.
 */
public class DummyEvent extends Event {
    DummyEvent(Rectangle triggerArea) {
        super(triggerArea);
    }

    @Override
    public void execute(Session session, Game game) {

    }
}
