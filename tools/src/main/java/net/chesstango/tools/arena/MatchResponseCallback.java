package net.chesstango.tools.arena;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.match.MatchResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Mauricio Coria
 */

@Slf4j
class MatchResponseCallback implements Consumer<MatchResponse> {
    private static final String SESSION_ID = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));

    private final Path sessionDirectory;

    private MatchResponseCallback(Path sessionDirectory) {
        this.sessionDirectory = sessionDirectory;
    }

    static MatchResponseCallback open(Path matchStore) {
        Path sessionDirectory = matchStore.resolve(String.format("%s", SESSION_ID));
        try {
            Files.createDirectory(sessionDirectory);
            log.info("Session directory created: {}", sessionDirectory);
            return new MatchResponseCallback(sessionDirectory);
        } catch (FileAlreadyExistsException e) {
            System.err.printf("Session directory already exists %s\n", sessionDirectory.getFileName().toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(MatchResponse matchResponse) {
        String filename = String.format("match_%s.ser", UUID.randomUUID());
        Path filePath = sessionDirectory.resolve(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(matchResponse);
            log.info("Response serialized to file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to serialize response", e);
            throw new RuntimeException(e);
        }
    }
}
