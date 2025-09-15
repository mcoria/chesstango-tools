package net.chesstango.tools.epd.master;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.epd.EpdSearchResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Mauricio Coria
 */

@Slf4j
public class EpdSearchResponseCallback implements Consumer<EpdSearchResponse> {
    private final Path sessionDirectory;

    public EpdSearchResponseCallback(Path sessionDirectory) {
        this.sessionDirectory = sessionDirectory;
    }

    @Override
    public void accept(EpdSearchResponse epdSearchResponse) {
        String filename = String.format("epdSearch_%s.ser", UUID.randomUUID());
        Path filePath = sessionDirectory.resolve(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(epdSearchResponse);
            log.info("Response serialized to file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to serialize response", e);
            throw new RuntimeException(e);
        }
    }
}
