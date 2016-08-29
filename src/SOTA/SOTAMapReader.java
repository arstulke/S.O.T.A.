package SOTA;

import java.io.BufferedReader;
import java.io.*;

public class SOTAMapReader {

    char[][] map;

    void readMap(String path){
        File file = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            try {
                while ((line = br.readLine()) != null) {
                    if(!line.startsWith("&c")) {
                        for (int k = 0; k < line.toCharArray().length; k++) {
                            map[i][k] = line.toCharArray()[k];
                        }
                        i++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
