package game.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created Reader in game.util
 * by ARSTULKE on 24.01.2017.
 */
public class Reader {
    private final InputStreamReader inputStream;

    public Reader(InputStreamReader inputStream) {
        this.inputStream = inputStream;
    }


    public String read() throws IOException {
        String content = "", line;
        BufferedReader br = new BufferedReader(inputStream);
        while ((line = br.readLine()) != null) {
            content += line + "\n";
        }
        br.close();
        return content;
    }
}
