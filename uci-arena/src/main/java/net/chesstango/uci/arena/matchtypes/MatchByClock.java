package net.chesstango.uci.arena.matchtypes;

import net.chesstango.uci.gui.Controller;
import net.chesstango.uci.protocol.requests.ReqGo;
import net.chesstango.uci.protocol.requests.go.ReqGoFast;
import net.chesstango.uci.protocol.responses.RspBestMove;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Mauricio Coria
 */
public class MatchByClock implements MatchType {
    private final int inc;
    private int wTime;
    private int bTime;

    public MatchByClock(int time, int inc) {
        this.wTime = time;
        this.bTime = time;
        this.inc = inc;
    }


    @Override
    public RspBestMove retrieveBestMoveFromController(Controller currentTurn, boolean isWhite) {
        ReqGo ReqGo = new ReqGoFast()
                .setWTime(wTime)
                .setBTime(bTime)
                .setWInc(inc)
                .setBInc(inc);

        Instant start = Instant.now();

        RspBestMove bestMove = currentTurn.send_ReqGo(ReqGo);

        long timeElapsed = Duration.between(start, Instant.now()).toMillis();

        if (isWhite) {
            wTime -= (int) timeElapsed;
            wTime += inc;
            if (wTime < 0) {
                throw new RuntimeException("White time out");
            }
        } else {
            bTime -= (int) timeElapsed;
            bTime += inc;
            if (bTime < 0) {
                throw new RuntimeException("Black time out");
            }
        }

        return bestMove;
    }
}
