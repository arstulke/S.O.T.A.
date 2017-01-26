package application;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * Created CommandLine in application
 * by ARSTULKE on 26.01.2017.
 */
class CommandLine extends Thread {
    CommandLine() {
        super(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String line = br.readLine();
                    if (line.equals("reload")) {
                        Application.gameloader.reload(true, true);
                        Application.textureLoader.reload();
                    } else if (line.equals("stop")) {
                        System.exit(0);
                    } else if (line.startsWith("verify")) {
                        String token = line.split(" ")[1];
                        Log.getLogger(Thread.class).info(verifyMap(token));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static String verifyMap(String token) throws IOException {
        File dir = Paths.get(System.getProperty("user.dir")).resolve("unverifiedMaps/" + token).toFile();
        String title;
        if (dir.exists() && dir.isDirectory()) {
            title = Application.gameloader.getBuilder(token).getTitle();
            File dest = Paths.get(System.getProperty("user.dir")).resolve("maps/" + title).toFile();
            if (!dest.exists()) {
                FileUtils.moveDirectory(dir, dest);

                Application.gameloader.reload(false, true);
                Application.textureLoader.reload();
            } else {
                return "error";
            }
        } else {
            return "This map doesn't exists";
        }

        return "The Map (\"" + title + "\") has been successfully verified.";
    }
}
