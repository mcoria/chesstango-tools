package net.chesstango.uci.gui.states;

import net.chesstango.uci.protocol.UCIGui;
import net.chesstango.uci.protocol.responses.*;

/**
 * @author Mauricio Coria
 */
public class NoWaitRsp implements UCIGui {
    @Override
    public void do_uciOk(RspUciOk rspUciOk) {
    }

    @Override
    public void do_readyOk(RspReadyOk rspReadyOk) {
    }

    @Override
    public void do_bestMove(RspBestMove rspBestMove) {
    }

    @Override
    public void do_info(RspInfo rspInfo) {

    }

    @Override
    public void do_id(RspId rspId) {
    }
}
