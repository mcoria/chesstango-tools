package net.chesstango.uci.gui.states;

import net.chesstango.uci.gui.ControllerAbstract;
import net.chesstango.uci.protocol.UCIGui;
import net.chesstango.uci.protocol.responses.*;

/**
 * @author Mauricio Coria
 */
public class WaitRspUciOk implements UCIGui {
    private final ControllerAbstract controllerAbstract;

    public WaitRspUciOk(ControllerAbstract controllerAbstract) {
        this.controllerAbstract = controllerAbstract;
    }

    @Override
    public void do_uciOk(RspUciOk rspUciOk) {
        controllerAbstract.responseReceived(new NoWaitRsp(), rspUciOk);
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
        if (RspId.RspIdType.NAME.equals(rspId.getIdType()) && controllerAbstract.getEngineName() == null) {
            controllerAbstract.setEngineName(rspId.getText());
        }
        if (RspId.RspIdType.AUTHOR.equals(rspId.getIdType())) {
            controllerAbstract.setEngineAuthor(rspId.getText());
        }
    }

}
