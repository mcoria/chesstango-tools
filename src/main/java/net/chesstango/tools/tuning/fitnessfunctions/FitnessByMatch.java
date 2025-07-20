package net.chesstango.tools.tuning.fitnessfunctions;

import net.chesstango.board.Game;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.engine.Tango;
import net.chesstango.evaluation.Evaluator;
import net.chesstango.gardel.pgn.PGN;
import net.chesstango.gardel.pgn.PGNStringDecoder;
import net.chesstango.search.DefaultSearch;
import net.chesstango.tools.MatchMain;
import net.chesstango.uci.arena.gui.ControllerFactory;
import net.chesstango.uci.arena.gui.ControllerPoolFactory;
import net.chesstango.uci.arena.MatchMultiple;
import net.chesstango.uci.arena.MatchResult;
import net.chesstango.uci.gui.*;
import net.chesstango.uci.arena.listeners.MatchBroadcaster;
import net.chesstango.uci.arena.listeners.SavePGNGame;
import net.chesstango.uci.arena.matchtypes.MatchByDepth;
import net.chesstango.uci.arena.matchtypes.MatchType;
import net.chesstango.uci.engine.UciTango;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Mauricio Coria
 */
public class FitnessByMatch implements FitnessFunction {
    private static final MatchType MATCH_TYPE = new MatchByDepth(3);

    private static final String ENGINE_NAME = "TANGO";

    private ObjectPool<Controller> opponentPool;

    private Stream<FEN> fenList;


    @Override
    public void start() {
        Supplier<Controller> opponentSupplier = () -> ControllerFactory.createProxyController("Spike", null);

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
    public long fitness(Supplier<Evaluator> tangoEvaluatorSupplier) {
        Supplier<Controller> tangoEngineSupplier = () ->
                new ControllerTango(new UciTango(new Tango(new DefaultSearch(tangoEvaluatorSupplier.get()))))
                        .overrideEngineName(ENGINE_NAME);

        List<MatchResult> matchResult = fitnessEval(tangoEngineSupplier);

        return calculatePoints(matchResult);
    }


    private List<MatchResult> fitnessEval(Supplier<Controller> tangoEngineSupplier) {
        try (ObjectPool<Controller> tangoPool = new GenericObjectPool<>(new ControllerPoolFactory(tangoEngineSupplier))) {
            return new MatchMultiple(tangoPool, opponentPool, MATCH_TYPE)
                    .setSwitchChairs(true)
                    .setMatchListener(new MatchBroadcaster()
                            .addListener(new SavePGNGame()))
                    .play(fenList);
        }
    }


    protected long calculatePoints(List<MatchResult> matchResult) {
        long pointsWhiteWin = matchResult.stream()
                .filter(result -> ENGINE_NAME.equals(result.getEngineWhite().getEngineName()) && result.getEngineWhite() == result.getWinner())
                .count();

        long pointsWhiteLost = matchResult.stream()
                .filter(result -> ENGINE_NAME.equals(result.getEngineWhite().getEngineName()) && result.getEngineBlack() == result.getWinner())
                .count();

        long pointsBlackWin = matchResult.stream()
                .filter(result -> ENGINE_NAME.equals(result.getEngineBlack().getEngineName()) && result.getEngineBlack() == result.getWinner())
                .count();

        long pointsBlackLost = matchResult.stream()
                .filter(result -> ENGINE_NAME.equals(result.getEngineBlack().getEngineName()) && result.getEngineWhite() == result.getWinner())
                .count();

        return pointsWhiteWin - pointsWhiteLost + pointsBlackWin - pointsBlackLost;
    }

}
