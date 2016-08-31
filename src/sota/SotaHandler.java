package sota;

import java.awt.*;

import static javafx.scene.input.KeyCode.Y;

public class SotaHandler {

    boolean right;
    boolean left;
    boolean jump;

    private String[][] map;
    private char player;
    private Point position;


    private int width = 32;
    private int height = 9;

    public SotaHandler() {
        SOTAMapReader mr = new SOTAMapReader();
        mr.readMap(System.getProperty("user.dir") + "\\map.txt");

        player = mr.getPlayer();
        position = mr.locatePlayer();
        map = mr.getMap();
    }

    public String handle(boolean[] keys) {
        updateKeysBooleans(keys);

        checkMovement();
        checkEvents();

        return display();
    }

    private void checkMovement() {
        if (right && !left && !jump) {
            position.move(position.x, position.y + 1);
        }

        if (!right && left && !jump) {
            position.move(position.x, position.y - 1);
        }
    }

    private void checkEvents() {

    }

    private void updateKeysBooleans(boolean[] keys) {
        right = keys[0] || keys[1];
        left = keys[2] || keys[3];
        jump = keys[4] || keys[5] || keys[6];
    }

    private String display() {
        String[][] d = new String[width][height];

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                try {
                    int Y = x + position.x - 6;
                    int X = y + position.y - 1;

                    if (new Point(Y, X).equals(position)) {
                        d[y][x] = String.valueOf(player);
                    } else if (String.valueOf(map[Y][X]) == null) {
                        d[y][x] = String.valueOf(map[Y][X]);
                    } else {
                        d[y][x] = String.valueOf(map[Y][X]);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    d[y][x] = "§";
                    System.out.println("ArrayIndexOutOfBoundException: " + e.getMessage());
                }

                if(d[y][x] == null)
                    d[y][x] = " ";
            }
        }

        String display = "";
        String line;

        for (int x = 0; x < height; x++) {
            line = "";
            for (int y = 0; y < width; y++) {
                line += d[y][x];
            }
            display += line + "\n";
        }


        display = display.substring(0, display.length() - 1);

        return display;
    }
}
