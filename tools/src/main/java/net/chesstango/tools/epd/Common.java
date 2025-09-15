package net.chesstango.tools.epd;

import net.chesstango.engine.Tango;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
public class Common {

    public static final String SEARCH_SESSION_ID = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));

    public static Path createSessionDirectory(Path suiteDirectory, int depth) {
        Path sessionDirectory = suiteDirectory.resolve(String.format("depth-%d-%s-%s", depth, SEARCH_SESSION_ID, Tango.ENGINE_VERSION));

        try {
            Files.createDirectory(sessionDirectory);
        } catch (FileAlreadyExistsException e) {
            System.err.printf("Session directory already exists %s\n", sessionDirectory.getFileName().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sessionDirectory;
    }

    public static List<Path> getEpdFiles(Path suiteDirectory, String filePattern) {
        String finalPattern = filePattern.replace(".", "\\.").replace("*", ".*");
        Predicate<String> matchPredicate = Pattern.compile(finalPattern).asMatchPredicate();
        try (Stream<Path> stream = Files.list(suiteDirectory)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> matchPredicate.test(file.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
