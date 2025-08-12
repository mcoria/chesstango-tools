package net.chesstango.tools.worker.match;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.uci.arena.Match;
import net.chesstango.uci.arena.MatchResult;
import net.chesstango.uci.arena.matchtypes.MatchType;
import net.chesstango.uci.gui.Controller;

import java.util.function.Function;

/**
 * @author Mauricio Coria
 */
@Slf4j
class MatchWorker implements Function<MatchRequest, MatchResponse> {

    private final ControllerProvider controllerProvider;

    MatchWorker(ControllerProvider controllerProvider) {
        this.controllerProvider = controllerProvider;
    }

    @Override
    public MatchResponse apply(MatchRequest matchRequest) {
        Controller whiteController = controllerProvider.getController(matchRequest.getWhiteEngineName());

        Controller blackController = controllerProvider.getController(matchRequest.getBlackEngineName());

        MatchType matchType = matchRequest.getMatchType();

        FEN fen = FEN.of(matchRequest.getFen());

        Match match = new Match(whiteController, blackController, fen, matchType);

        MatchResult result = match.play();

        return new MatchResponse()
                .setWhiteEngineName(matchRequest.getWhiteEngineName())
                .setBlackEngineName(matchRequest.getBlackEngineName())
                .setFen(matchRequest.getFen())
                .setMatchResult(result);
    }

}
