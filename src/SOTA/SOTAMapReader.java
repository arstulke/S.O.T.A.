package SOTA;

import java.io.BufferedReader;
import java.io.*;

public class SOTAMapReader {

    static String[][] map;
    static int mapLength = 100;

    static String[][][] eventMap;
    static char player = 'X';

    public static void readMap(String path) {
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

    public char getPlayer() {
        return player;
    }

    public static String[][][] getMap() {
        return eventMap;
    }
}
