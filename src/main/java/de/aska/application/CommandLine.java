package de.aska.application;

import de.aska.game.util.ApplicationProperties;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.log.Log;
import spark.Spark;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Map;

/**
 * Created CommandLine in de.aska.application
 * by ARSTULKE on 26.01.2017.
 */
class CommandLine extends Thread {

    private final static ApplicationProperties properties = new ApplicationProperties();

    CommandLine() {
        super(() -> {
            Log.getLogger(Thread.class).info("Starting...");
            waitForSpark();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            Log.getLogger(Thread.class).info("System is ready for input...");
            Log.getLogger(Thread.class).info(sayHello());

            while (true) {
                String line = "Could not read Command.";
                try {
                    line = br.readLine();
                    if (line.equals("reload")) {
                        Application.reload(true);
                    } else if (line.equals("stop")) {
                        Log.getLogger(Thread.class).info("Have a nice day ;)");
                        System.exit(0);
                    } else if (line.startsWith("verify")) {
                        String token = line.split(" ")[1];
                        Log.getLogger(Thread.class).info(verifyMap(token));
                    } else if (line.startsWith("delete")) {
                        String token = line.split(" ")[1];
                        Log.getLogger(Thread.class).info(deleteMap(token));
                    } else if (line.startsWith("list")) {
                        Log.getLogger(Thread.class).info(listMap());
                    } else {
                        Log.getLogger(Thread.class).warn("Unknown Command - \"" + line + "\"");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.getLogger(Thread.class).warn("There is a problem with the Command \"" + line + "\" - Maybe there is a Missing Parameter");
                } catch (IOException e) {
                    Log.getLogger(Thread.class).warn("There is a problem with the Command \"" + line + "\" - " + e);
                }
            }
        });
    }

    private static void waitForSpark() {
        Spark.awaitInitialization();

        Application.reload(true);
    }

    private static String sayHello() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 11) {
            return "Good Morning.";
        } else if (timeOfDay >= 11 && timeOfDay < 13) {
            return "Have a nice lunch!";
        } else if (timeOfDay >= 13 && timeOfDay < 16) {
            return "Good Afternoon.";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return "Good Evening.";
        } else if ((timeOfDay >= 21 && timeOfDay < 24) || (timeOfDay >= 0 && timeOfDay < 6)) {
            return "Good Night.";
        }
        return "Hello and Welcome to S.O.T.A.";
    }

    private static String listMap() {
        StringBuilder output = new StringBuilder("");

        Map<String, String> maps = Application.gameloader.listUnverifiedMaps();
        maps.forEach((s, s2) -> output.append("-\t").append(s2).append("\n\t").append(s).append("\n"));

        return (maps.size() == 0) ? "No maps found to verify" : output.toString();
    }

    private static String deleteMap(String token) throws IOException {
        String title;
        File dir = properties.getUnverifiedMapsPath().resolve(token).toFile();
        if (dir.exists() && dir.isDirectory()) {
            title = Application.gameloader.getBuilder(token).getTitle();
            FileUtils.deleteDirectory(dir);

            Application.reload(false);

        } else {
            return "This map doesn't exists";
        }
        return "The Map (\"" + title + "\") has been successfully deleted.";
    }


    private static String verifyMap(String token) throws IOException {
        File dir = properties.getUnverifiedMapsPath().resolve(token).toFile();
        String title;
        if (dir.exists() && dir.isDirectory()) {
            title = Application.gameloader.getBuilder(token).getTitle();
            File dest = properties.getVerifiedMapsPath().resolve(title).toFile();
            if (!dest.exists()) {
                FileUtils.moveDirectory(dir, dest);

                Application.reload(false);
            } else {
                return "error";
            }
        } else {
            return "This map doesn't exists";
        }

        return "The Map (\"" + title + "\") has been successfully verified.";
    }
}
