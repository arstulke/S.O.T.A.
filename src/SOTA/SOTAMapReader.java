package sota;

import java.awt.*;
import java.io.BufferedReader;
import java.io.*;

class SOTAMapReader {

    private String[][] map;
    private int mapLength = 100;

    private String[][][] eventMap;
    private char player = 'X';

    void readMap(String path) {
        File file = new File(path);
        try  {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int i = 0;
            try {
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(":")) {
                        for (int k = 1; k < mapLength; k++) {
                            try {
                                map[i][k] = Character.toString(line.charAt(k));
                            }catch(Exception ignore){
                                map[i][k] = " ";
                            }
                        }
                        i++;
                    } else if (line.startsWith("player:")) {
                        player = line.split(":")[1].toCharArray()[0];
                    } else if (line.startsWith("-")) {
                       mapLength = line.length();
                    }

                }
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
        int x = 0;
        int y = 0;

        for(String[] ch : map){
            for(String c : ch){
                if(c.charAt(0) == player){
                    return new Point(x, y);
                }

                y++;
            }

            y = 0;
            x++;
        }

        return null;
    }
}
