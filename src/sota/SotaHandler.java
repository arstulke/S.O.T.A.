package sota;

import java.awt.*;

public class SotaHandler {

    public boolean right;
    public boolean left;
    public boolean jump;

    private String[][][] map;
    private char player;
    private Point position;

    public SotaHandler(){
        SOTAMapReader mr = new SOTAMapReader();
        mr.readMap(System.getProperty("user.dir") + "\\map.txt");

        player = mr.getPlayer();
        position = mr.locatePlayer();
        map = mr.getMap();
    }

    public String handle(boolean[] keys) {
        updateKeysBooleans(keys);

        String s = right + ", " + left + ", " + jump + "\n";
        s += player + ": " + position.x + ", " + position.y;

        return s;
    }

    private void updateKeysBooleans(boolean[] keys) {
        right = keys[0] || keys[1];
        left = keys[2] || keys[3];
        jump = keys[4] || keys[5] || keys[6];
    }

    private String[][] display(){
        //String[][] display = new String[][];
        return null;
    }
}
