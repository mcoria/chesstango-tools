package net.chesstango.tools.epd;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.epd.worker.EpdSearchResponse;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchMainReader {

    public static void main(String[] args) {
        Path sessionDirectory = Path.of("C:\\java\\projects\\chess\\chess-utils\\testing\\positions\\database\\depth-2-2025-09-27-08-34-v1.2.0-SNAPSHOT");

        Stream<EpdSearchResponse> matchResponses = loadEpdSearchResponse(sessionDirectory);

        EpdSearchReportSaver epdSearchReportSaver = new EpdSearchReportSaver(sessionDirectory);

        matchResponses.forEach(epdSearchResponse -> {
            epdSearchReportSaver.saveReport(epdSearchResponse.getSearchId(), epdSearchResponse.getEpdSearchResults());
        });

    }

    private static Stream<EpdSearchResponse> loadEpdSearchResponse(Path sessionDirectory) {
        File directory = sessionDirectory.toFile();

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));

        assert files != null;
        return Stream.of(files).map(file -> {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (EpdSearchResponse) ois.readObject();
            } catch (Exception e) {
                log.error("Failed to deserialize file: " + file, e);
                return null;
            }
        }).filter(Objects::nonNull);
    }
}
