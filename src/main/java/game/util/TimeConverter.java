package game.util;

/**
 * Created TimeConverter in game.util
 * by ARSTULKE on 07.02.2017.
 */
public class TimeConverter {
    public static int toTicks(int ms) {
        return ms / 100;
    }

    public static int toMS(int ticks) {
        return ticks * 100;
    }
}
