package game.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created Reader in game.util
 * by ARSTULKE on 24.01.2017.
 */
public class Reader {
    private final FileReader inputStream;

    public Reader(FileReader fileReader) {
        this.inputStream = fileReader;
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
