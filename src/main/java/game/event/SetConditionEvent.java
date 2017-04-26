package game.event;

import game.model.Game;
import network.Session;

import java.awt.*;

/**
 * Created SetConditionEvent in event.event
 * by ARSTULKE on 23.01.2017.
 */
public class SetConditionEvent extends Event {
    private final String name;
    private final String value;

    public SetConditionEvent(Rectangle triggerArea, boolean repeatable, String name, String value) {
        super(triggerArea, repeatable);
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(Session session, Game game) {
        game.setCondition(name, value);
    }
}
