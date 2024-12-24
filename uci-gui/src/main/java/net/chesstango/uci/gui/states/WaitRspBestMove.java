package net.chesstango.uci.gui.states;

import net.chesstango.uci.gui.ControllerAbstract;
import net.chesstango.uci.protocol.UCIGui;
import net.chesstango.uci.protocol.responses.*;

/**
 * @author Mauricio Coria
 */
public class WaitRspBestMove implements UCIGui {
    private final ControllerAbstract controllerAbstract;

    public WaitRspBestMove(ControllerAbstract controllerAbstract) {
        this.controllerAbstract = controllerAbstract;
    }

    @Override
    public void do_uciOk(RspUciOk rspUciOk) {
    }

    @Override
    public void do_readyOk(RspReadyOk rspReadyOk) {
    }

    @Override
    public void do_bestMove(RspBestMove rspBestMove) {
        controllerAbstract.responseReceived(rspBestMove);
        controllerAbstract.setCurrentState(new NoWaitRsp());
    }

    @Override
    public void do_info(RspInfo rspInfo) {
    }

    @Override
    public void do_id(RspId rspId) {
    }
}
