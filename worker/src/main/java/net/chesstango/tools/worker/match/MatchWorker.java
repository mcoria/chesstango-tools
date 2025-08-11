package net.chesstango.tools.worker.match;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.uci.arena.Match;
import net.chesstango.uci.arena.MatchResult;
import net.chesstango.uci.arena.matchtypes.MatchType;
import net.chesstango.uci.gui.Controller;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class MatchWorker {

    private final ControllerProvider controllerProvider;

    public MatchWorker(ControllerProvider controllerProvider) {
        this.controllerProvider = controllerProvider;
    }

    public MatchResponse run(MatchRequest request) {
        Controller whiteController = controllerProvider.getController(request.getWhiteEngineName());

        Controller blackController = controllerProvider.getController(request.getBlackEngineName());

        MatchType matchType = request.getMatchType();

        FEN fen = FEN.of(request.getFen());

        Match match = new Match(whiteController, blackController, fen, matchType);

        MatchResult result = match.play();

        return new MatchResponse()
                .setWhiteEngineName(request.getWhiteEngineName())
                .setBlackEngineName(request.getBlackEngineName())
                .setFen(request.getFen())
                .setMatchResult(result);
    }

}
