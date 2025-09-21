package net.chesstango.tools.arena;

import net.chesstango.tools.reports.arena.SummaryReport;
import net.chesstango.tools.worker.match.MatchResponse;
import net.chesstango.uci.arena.MatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
public class MatchMainReader {
    private static final Logger logger = LoggerFactory.getLogger(MatchMainReader.class);

    public static void main(String[] args) {
        List<MatchResponse> matchResponses = loadMatchResponses("C:\\java\\projects\\chess\\chess-utils\\testing\\matches\\2025-09-21-15-00");

        List<MatchResult> matchResult = matchResponses.stream().map(MatchResponse::getMatchResult).toList();

        new SummaryReport()
                .withMatchResults(matchResult)
                //.withMatchResult(List.of(engineController1, engineController2), matchResult)
                .printReport(System.out);


        /*
        new SessionReport()
                //.withCollisionStatistics()
                //.withNodesVisitedStatistics()
                //.withCutoffStatistics()
                .breakByColor()
                .withMathResults(matchResult)
                .printReport(System.out);


        new SearchesReport()
                //.withCutoffStatistics()
                //.withNodesVisitedStatistics()
                .withPrincipalVariation()
                .withMathResults(matchResult)
                .printReport(System.out);
         */
    }

    public static List<MatchResponse> loadMatchResponses(String directoryStr) {
        List<MatchResponse> matchResponses = new LinkedList<>();

        Path directory = Path.of(directoryStr);

        try (Stream<Path> files = Files.list(directory)) {
            files.forEach(file -> {
                try {
                    logger.info("File: {}", file.getFileName());
                    MatchResponse matchResponse = deserializeFromFile(file);

                    matchResponses.add(matchResponse);
                } catch (IOException e) {
                    logger.error("Error reading file: " + file.getFileName(), e);
                }
            });
        } catch (IOException e) {
            logger.error("Error listing directory: " + directory, e);
        }

        return matchResponses;
    }

    private static MatchResponse deserializeFromFile(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (MatchResponse) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error deserializing object", e);
        }
    }
    
}
