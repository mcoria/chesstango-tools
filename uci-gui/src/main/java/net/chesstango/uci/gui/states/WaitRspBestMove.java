package net.chesstango.uci.gui.states;

import net.chesstango.uci.gui.ControllerAbstract;
import net.chesstango.goyeneche.UCIGui;
import net.chesstango.goyeneche.responses.*;

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
    public void do_option(RspOption rspOption) {

    }

    @Override
    public void do_readyOk(RspReadyOk rspReadyOk) {
    }

    @Override
    public void do_bestMove(RspBestMove rspBestMove) {
        controllerAbstract.responseReceived(rspBestMove);
    }

    @Override
    public void do_info(RspInfo rspInfo) {
    }

    @Override
    public void do_id(RspId rspId) {
    }
}
