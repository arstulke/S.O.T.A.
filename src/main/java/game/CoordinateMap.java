package game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created CoordinateMap.java in game
 * by Arne on 13.01.2017.
 */
public class CoordinateMap<E> {
    private final Map<Point, E> elements = new ConcurrentHashMap<>();

    public CoordinateMap(Map<Point, E> values) {
        this.elements.putAll(values);
    }

    public E get(Point p) {
        return elements.get(p);
    }

    public E get(int x, int y) {
        return get(new Point(x, y));
    }

    public Map<Point, E> copy() {
        return new HashMap<>(elements);
    }
}
