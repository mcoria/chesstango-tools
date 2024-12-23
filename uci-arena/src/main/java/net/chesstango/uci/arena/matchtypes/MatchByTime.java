package net.chesstango.uci.arena.matchtypes;

import net.chesstango.uci.gui.Controller;
import net.chesstango.uci.protocol.requests.CmdGo;
import net.chesstango.uci.protocol.requests.go.CmdGoTime;
import net.chesstango.uci.protocol.responses.RspBestMove;

/**
 * @author Mauricio Coria
 */
public class MatchByTime implements MatchType {
    public final CmdGo cmdGo;

    public MatchByTime(int timeOut) {
        this.cmdGo = new CmdGoTime().setTimeOut(timeOut);
    }

    @Override
    public RspBestMove retrieveBestMoveFromController(Controller currentTurn, boolean isWhite) {
        return currentTurn.send_CmdGo(cmdGo);
    }
}
