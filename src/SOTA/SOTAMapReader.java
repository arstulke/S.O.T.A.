package sota;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class SOTAMapReader {

    private String[][] map;
    private int width = 0;
    private int height = 0;


    private String[][][] eventMap;
    private char player = 'X';

    void readMap(String path) {
        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int i = 0;
            try {
                height = 0;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(":")) {
                        height++;
                    }
                }
                br.close();
                br = new BufferedReader(new FileReader(file));

                while ((line = br.readLine()) != null) {
                    if (line.startsWith(":")) {
                        for (int k = 1; k < width; k++) {
                            try {
                                map[i][k] = Character.toString(line.charAt(k));
                            } catch (Exception ignore) {
                                map[i][k] = " ";
                            }
                        }
                        i++;
                    } else if (line.startsWith("player:")) {
                        player = line.split(":")[1].toCharArray()[0];
                    } else if (line.startsWith("-")) {
                        width = line.length();
                        map = new String[height][width];
                    }

                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    char getPlayer() {
        return player;
    }

    String[][][] getMap() {
        return eventMap;
    }

    Point locatePlayer() {
        int y = 0;
        int x = 0;

        for (String[] ch : map) {
            for (String c : ch) {
                if (c != null && c.equals(String.valueOf(player))) {
                    return new Point(y, x);
                }

                x++;
            }

            x = 0;
            y++;
        }

        return null;
    }
}
