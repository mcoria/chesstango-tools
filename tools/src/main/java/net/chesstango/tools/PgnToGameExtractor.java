package net.chesstango.tools;


import net.chesstango.board.Color;
import net.chesstango.board.Game;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.gardel.pgn.PGN;
import net.chesstango.gardel.pgn.PGNDecoder;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author Mauricio Coria
 */
public class PgnToGameExtractor {

    /**
     * Run with -i C:\java\projects\chess\chess-utils\testing\positions\players\Kasparov.pgn
     *
     */
    public static void main(String[] args) {

        PgnToGameExtractor PgnToGameExtractor = new PgnToGameExtractor();

        CommandLine parsedArgs = parseArguments(args);

        try (InputStream inputStream = parsedArgs.hasOption('i')
                ? new FileInputStream(parsedArgs.getOptionValue('i'))
                : System.in) {

            PgnToGameExtractor.process(inputStream, System.out, System.err);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void process(InputStream inputStream, PrintStream out, PrintStream err) throws IOException {
        Evaluator evaluator = Evaluator.createInstance();
        PGNDecoder pgnStringDecoder = new PGNDecoder();
        pgnStringDecoder.decodePGNs(inputStream)
                .flatMap(PGN::toFEN)
                .map(Game::from)
                .filter(game -> !game.getStatus().isFinalStatus())
                .filter(this::filterGame)
                //.peek(game -> System.out.printf("%s\teval:", game))
                .peek(evaluator::setGame)
                .mapToInt(_ -> Math.abs(evaluator.evaluate()))
                .forEach(out::println);
    }

    private boolean filterGame(Game game) {
        long whitePosition = game.getPosition().getPositions(Color.WHITE);
        long blackPosition = game.getPosition().getPositions(Color.BLACK);

        long whitePawns = game.getPosition().getPawnPositions() & whitePosition;
        long blackPawns = game.getPosition().getPawnPositions() & blackPosition;
        long pawnsDifference = Math.abs(Long.bitCount(whitePawns) - Long.bitCount(blackPawns));

        long whiteKnights = game.getPosition().getKnightPositions() & whitePosition;
        long blackKnights = game.getPosition().getKnightPositions() & blackPosition;
        long knightsDifference = Math.abs(Long.bitCount(whiteKnights) - Long.bitCount(blackKnights));

        long whiteBishops = game.getPosition().getBishopPositions() & whitePosition;
        long blackBishops = game.getPosition().getBishopPositions() & blackPosition;
        long bishopsDifference = Math.abs(Long.bitCount(whiteBishops) - Long.bitCount(blackBishops));

        long whiteRooks = game.getPosition().getRookPositions() & whitePosition;
        long blackRooks = game.getPosition().getRookPositions() & blackPosition;
        long rooksDifference = Math.abs(Long.bitCount(whiteRooks) - Long.bitCount(blackRooks));

        long whiteQueens = game.getPosition().getQueenPositions() & whitePosition;
        long blackQueens = game.getPosition().getQueenPositions() & blackPosition;
        long queensDifference = Math.abs(Long.bitCount(whiteQueens) - Long.bitCount(blackQueens));

        long totalDifference = pawnsDifference + knightsDifference + bishopsDifference + rooksDifference + queensDifference;

        return Math.abs(Long.bitCount(whitePosition) - Long.bitCount(blackPosition)) == 1 &&
                totalDifference == 1;
    }


    private static CommandLine parseArguments(String[] args) {
        final Options options = new Options();
        Option inputOpt = Option.builder("i")
                .argName("input")
                .hasArg()
                .desc("input file")
                .build();
        options.addOption(inputOpt);
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            return parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(-1);
        }
        return null;
    }
}
