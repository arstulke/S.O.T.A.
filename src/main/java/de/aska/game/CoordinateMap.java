package de.aska.game;

import java.awt.*;
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

    private CoordinateMap(Map<Point, E> values) {
        this.elements.putAll(values);
    }

    public CoordinateMap() {
    }

    public E get(Point p) {
        return elements.get(p);
    }

    public void set(Point point, E element) {
        elements.put(point, element);
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

    public static class Builder<T> {
        private final Map<Point, T> elements = new ConcurrentHashMap<>();

        public CoordinateMap<T> build() {
            return new CoordinateMap<>(elements);
        }

        public Stream<Map.Entry<Point, T>> stream() {
            return elements.entrySet().stream();
        }

        public void put(Point p, T element) {
            elements.put(p, element);
        }

        public boolean containsKey(Point point) {
            return elements.containsKey(point);
        }

        public T get(Point point) {
            return elements.get(point);
        }
    }
}
