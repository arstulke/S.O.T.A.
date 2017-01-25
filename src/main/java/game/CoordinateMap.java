package game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Created CoordinateMap.java in game
 * by Arne on 13.01.2017.
 */
public class CoordinateMap<E> {
    private final Map<Point, E> elements = new ConcurrentHashMap<>();

    public CoordinateMap(Map<Point, E> values) {
        this.elements.putAll(values);
    }

    public CoordinateMap() {
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

    public void set(Point point, E element) {
        elements.put(point, element);
    }

    public void set(int y, int x, E element) {
        set(new Point(x, y), element);
    }

    public boolean containsKey(Point point) {
        return elements.containsKey(point);
    }

    public void forEach(BiConsumer<? super Point, ? super E> biConsumer) {
        elements.forEach(biConsumer);
    }

    public void clear() {
        elements.clear();
    }

    public Stream<Map.Entry<Point, E>> stream() {
        return elements.entrySet().stream();
    }
}
