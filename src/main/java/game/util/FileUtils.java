package game.util;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created FileUtils in game.util
 * by ARSTULKE on 03.02.2017.
 */
public class FileUtils {
    public static List<File> listFiles(Path path) {
        File[] files = path.toFile().listFiles();
        return toList(files);
    }

    public static List<File> listFiles(Path path, FilenameFilter filenameFilter) {
        File[] files = path.toFile().listFiles(filenameFilter);
        return toList(files);
    }

    private static List<File> toList(File[] files) {
        if (files != null) {
            return Arrays.asList(files);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Path> listPaths(Path path) {
        return mapToPath(listFiles(path).stream())
                .collect(Collectors.toList());
    }

    private static Stream<Path> mapToPath(Stream<File> stream) {
        return stream.map(file -> Paths.get(file.toURI()));
    }
}