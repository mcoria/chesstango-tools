package net.chesstango.tools.tuning.fitnessfunctions;

import net.chesstango.board.Game;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.gardel.pgn.PGN;
import net.chesstango.gardel.pgn.PGNStringDecoder;
import net.chesstango.tools.MatchMain;
import net.chesstango.tools.arena.ControllerPoolFactory;
import net.chesstango.tools.arena.MatchMultiple;
import net.chesstango.uci.arena.ControllerFactory;
import net.chesstango.uci.arena.MatchResult;
import net.chesstango.uci.arena.listeners.MatchBroadcaster;
import net.chesstango.uci.arena.listeners.SavePGNGame;
import net.chesstango.uci.arena.matchtypes.MatchByDepth;
import net.chesstango.uci.arena.matchtypes.MatchType;
import net.chesstango.uci.gui.Controller;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
public class FitnessByMatch implements FitnessFunction {
    private static final MatchType MATCH_TYPE = new MatchByDepth(3);

    private static final String ENGINE_NAME = "TANGO";

    private static final Path spike = Path.of("C:\\java\\projects\\chess\\chess-utils\\engines\\catalog\\Spike.json");

    //private static final int parallelJobs = Runtime.getRuntime().availableProcessors();
    private static final int parallelJobs = 2;

    private ObjectPool<Controller> opponentPool;

    private Stream<FEN> fenList;


    @Override
    public void start() {
        Supplier<Controller> opponentSupplier = () -> ControllerFactory.createProxyController(spike);

        Stream<PGN> pgnGames = new PGNStringDecoder().decodePGNs(MatchMain.class.getClassLoader().getResourceAsStream("Balsa_Top10.pgn"));
        //this.fenList = new Transcoding().pgnFileToFenPositions(FitnessByMatch.class.getClassLoader().getResourceAsStream("Balsa_Top25.pgn"));
        //this.fenList = new Transcoding().pgnFileToFenPositions(FitnessByMatch.class.getClassLoader().getResourceAsStream("Balsa_Top50.pgn"));
        //this.fenList = new Transcoding().pgnFileToFenPositions(FitnessByMatch.class.getClassLoader().getResourceAsStream("Balsa_v500.pgn"));

        this.fenList = pgnGames.map(Game::from).map(Game::getCurrentFEN);
        this.opponentPool = new GenericObjectPool<>(new ControllerPoolFactory(opponentSupplier));
    }

    @Override
    public void stop() {
        opponentPool.close();
    }

    @Override
    public long fitness(Supplier<Evaluator> gameEvaluatorSupplier) {
        Supplier<Controller> tangoEngineSupplier = () -> ControllerFactory.createTangoControllerWithEvaluator(gameEvaluatorSupplier)
                .overrideEngineName(ENGINE_NAME);

        List<MatchResult> matchResult = fitnessEval(tangoEngineSupplier);

        return calculatePoints(matchResult);
    }


    private List<MatchResult> fitnessEval(Supplier<Controller> tangoEngineSupplier) {
        try (ObjectPool<Controller> tangoPool = new GenericObjectPool<>(new ControllerPoolFactory(tangoEngineSupplier))) {
            return new MatchMultiple(parallelJobs, tangoPool, opponentPool, MATCH_TYPE)
                    .setSwitchChairs(true)
                    .setMatchListener(new MatchBroadcaster()
                            .addListener(new SavePGNGame()))
                    .play(fenList);
        }
    }


    protected long calculatePoints(List<MatchResult> matchResult) {
        long pointsWhiteWin = matchResult.stream()
                .map(MatchResult::pgn)
                .filter(pgn -> ENGINE_NAME.equals(pgn.getWhite()))
                .filter(pgn-> Objects.equals(PGN.Result.WHITE_WINS, pgn.getResult()))
                .count();

        long pointsWhiteLost = matchResult.stream()
                .map(MatchResult::pgn)
                .filter(pgn -> ENGINE_NAME.equals(pgn.getWhite()))
                .filter(pgn-> Objects.equals(PGN.Result.BLACK_WINS, pgn.getResult()))
                .count();

        long pointsBlackWin = matchResult.stream()
                .map(MatchResult::pgn)
                .filter(pgn -> ENGINE_NAME.equals(pgn.getBlack()))
                .filter(pgn-> Objects.equals(PGN.Result.BLACK_WINS, pgn.getResult()))
                .count();

        long pointsBlackLost = matchResult.stream()
                .map(MatchResult::pgn)
                .filter(pgn -> ENGINE_NAME.equals(pgn.getBlack()))
                .filter(pgn-> Objects.equals(PGN.Result.WHITE_WINS, pgn.getResult()))
                .count();

        return pointsWhiteWin - pointsWhiteLost + pointsBlackWin - pointsBlackLost;
    }

}
