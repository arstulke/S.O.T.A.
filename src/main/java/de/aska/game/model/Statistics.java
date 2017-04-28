package de.aska.game.model;

/**
 * Created Statistics.java in game.model
 * by Arne on 13.01.2017.
 */
@SuppressWarnings("SimplifiableIfStatement")
public class Statistics {
    private final int ticks;
    private final int fails;

    Statistics(int ticks, int fails) {
        this.ticks = ticks;
        this.fails = fails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistics that = (Statistics) o;

        if (ticks != that.ticks) return false;
        return fails == that.fails;
    }

    @Override
    public int hashCode() {
        int result = ticks;
        result = 31 * result + fails;
        return result;
    }

    public int getFails() {
        return fails;
    }

    public int getTicks() {
        return ticks;
    }
}
