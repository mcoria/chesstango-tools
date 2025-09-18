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
class Common {

    static final String SEARCH_SESSION_DATE = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));

    static String createSessionId(int depth) {
        return String.format("depth-%d-%s-%s", depth, SEARCH_SESSION_DATE, Tango.ENGINE_VERSION);
    }

    static Path createSessionDirectory(Path suiteDirectory, int depth) {
        String sessionId = createSessionId(depth);
        return createSessionDirectory(suiteDirectory, sessionId);
    }

    static Path createSessionDirectory(Path suiteDirectory, String sessionId) {
        Path sessionDirectory = suiteDirectory.resolve(sessionId);

        try {
            Files.createDirectory(sessionDirectory);
        } catch (FileAlreadyExistsException e) {
            System.err.printf("Session directory already exists %s\n", sessionDirectory.getFileName().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sessionDirectory;
    }

    static List<Path> listEpdFiles(Path suiteDirectory, String filePattern) {
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
