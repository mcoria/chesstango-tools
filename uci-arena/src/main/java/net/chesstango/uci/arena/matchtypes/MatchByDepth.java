package net.chesstango.uci.arena.matchtypes;

import net.chesstango.uci.gui.Controller;
import net.chesstango.uci.protocol.requests.ReqGo;
import net.chesstango.uci.protocol.requests.go.ReqGoDepth;
import net.chesstango.uci.protocol.responses.RspBestMove;

/**
 * @author Mauricio Coria
 */
public class MatchByDepth implements MatchType {
    public final ReqGo ReqGo;

    public MatchByDepth(int depth) {
        this.ReqGo = new ReqGoDepth().setDepth(depth);
    }

    @Override
    public RspBestMove retrieveBestMoveFromController(Controller currentTurn, boolean isWhite) {
        return currentTurn.send_ReqGo(ReqGo);
    }
}
