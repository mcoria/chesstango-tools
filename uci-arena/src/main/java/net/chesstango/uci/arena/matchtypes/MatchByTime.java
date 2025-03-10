package net.chesstango.uci.arena.matchtypes;

import net.chesstango.uci.gui.Controller;
import net.chesstango.uci.protocol.requests.ReqGo;
import net.chesstango.uci.protocol.requests.go.ReqGoTime;
import net.chesstango.uci.protocol.responses.RspBestMove;

/**
 * @author Mauricio Coria
 */
public class MatchByTime implements MatchType {
    public final ReqGo ReqGo;

    public MatchByTime(int timeOut) {
        this.ReqGo = new ReqGoTime().setTimeOut(timeOut);
    }

    @Override
    public RspBestMove retrieveBestMoveFromController(Controller currentTurn, boolean isWhite) {
        return currentTurn.send_ReqGo(ReqGo);
    }
}
