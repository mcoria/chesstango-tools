package net.chesstango.tools;


import net.chesstango.board.Game;
import net.chesstango.board.moves.Move;
import net.chesstango.board.moves.MoveCaptureEnPassant;
import net.chesstango.board.representations.move.TangoMoveSupplier;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.gardel.move.SANDecoder;
import net.chesstango.gardel.pgn.PGN;
import net.chesstango.gardel.pgn.PGNDecoder;
import net.chesstango.gardel.pgn.PGNMove;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
public class GameDiffExtractor {

    /**
     * Run with -i C:\java\projects\chess\chess-utils\testing\positions\players\Kasparov.pgn
     *
     */
    public static void main(String[] args) {

        GameDiffExtractor GameExtractor = new GameDiffExtractor();

        CommandLine parsedArgs = parseArguments(args);

        try (InputStream inputStream = parsedArgs.hasOption('i')
                ? new FileInputStream(parsedArgs.getOptionValue('i'))
                : System.in) {

            GameExtractor.process(inputStream, System.out, System.err);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Evaluator evaluator = Evaluator.createInstance();

    private void process(InputStream inputStream, PrintStream out, PrintStream err) throws IOException {
        PGNDecoder pgnStringDecoder = new PGNDecoder();
        pgnStringDecoder.decodePGNs(inputStream)
                .flatMap(this::toIntegerDifference)
                //.filter(moveDiff -> moveDiff.move().getTo().piece() != null &&  moveDiff.move().getTo().piece().isQueen())
                //.filter(moveDiff -> moveDiff.move() instanceof MovePromotion)
                .filter(moveDiff -> moveDiff.move() instanceof MoveCaptureEnPassant)
                .sorted((o1, o2) -> Integer.compare(o1.evalDiff(), o2.evalDiff()))
                .forEach(out::println);
    }

    private Stream<MoveDiff> toIntegerDifference(PGN pgn) {
        Stream.Builder<MoveDiff> streamBuilder = Stream.builder();

        FEN fen = pgn.getFen() == null ? FEN.START_POSITION : pgn.getFen();
        Game game = Game.from(fen);
        evaluator.setGame(game);
        SANDecoder<Move> sanDecoder = new SANDecoder<>(new TangoMoveSupplier(game));


        AtomicInteger currentEval = new AtomicInteger(evaluator.evaluate());
        pgn.getPgnMoves()
                .stream()
                .map(PGNMove::getSanMove)
                .forEach(moveStr -> {
                    if (game.getState().getStatus().isInProgress()) {
                        Move move = sanDecoder.decode(moveStr, game.toFEN());
                        if (move != null) {
                            FEN currentFen = game.toFEN();
                            move.executeMove();
                            int newEval = evaluator.evaluate();
                            if (!move.isQuiet() && game.getState().getStatus().isInProgress()) {
                                int diff = Math.abs(newEval - currentEval.get());
                                streamBuilder.accept(new MoveDiff(currentFen, move, diff));
                            }
                            currentEval.set(newEval);
                        } else {
                            throw new RuntimeException(String.format("[%s] %s is not in the list of legal moves for %s", pgn.getEvent(), moveStr, game.toFEN().toString()));
                        }
                    }
                });

        return streamBuilder.build();
    }


    record MoveDiff(FEN fen, Move move, int evalDiff) {
        @Override
        public String toString() {
            return String.format("%s\t%s\t%d", fen, move.coordinateEncoding(), evalDiff);
        }
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
