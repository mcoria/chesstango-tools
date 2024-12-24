package net.chesstango.uci.gui;


import lombok.Setter;
import net.chesstango.uci.gui.states.NoWaitRsp;
import net.chesstango.uci.gui.states.WaitRspBestMove;
import net.chesstango.uci.gui.states.WaitRspReadyOk;
import net.chesstango.uci.gui.states.WaitRspUciOk;
import net.chesstango.uci.protocol.UCIGui;
import net.chesstango.uci.protocol.UCIRequest;
import net.chesstango.uci.protocol.UCIResponse;
import net.chesstango.uci.protocol.UCIService;
import net.chesstango.uci.protocol.requests.*;
import net.chesstango.uci.protocol.responses.*;
import net.chesstango.uci.protocol.stream.UCIOutputStreamGuiExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mauricio Coria
 */
public abstract class ControllerAbstract implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAbstract.class);

    private final UCIService service;

    private volatile UCIGui currentState;
    private volatile UCIResponse response;

    @Setter
    private String engineName;

    @Setter
    private String engineAuthor;

    private CmdGo cmdGo;

    public ControllerAbstract(UCIService service) {
        UCIGui messageExecutor = new UCIGui() {
            @Override
            public void do_uciOk(RspUciOk rspUciOk) {
                currentState.do_uciOk(rspUciOk);
            }

            @Override
            public void do_readyOk(RspReadyOk rspReadyOk) {
                currentState.do_readyOk(rspReadyOk);
            }

            @Override
            public void do_bestMove(RspBestMove rspBestMove) {
                currentState.do_bestMove(rspBestMove);
            }

            @Override
            public void do_info(RspInfo rspInfo) {
                currentState.do_info(rspInfo);
            }

            @Override
            public void do_id(RspId rspId) {
                currentState.do_id(rspId);
            }
        };

        this.service = service;
        this.service.setResponseOutputStream(new UCIOutputStreamGuiExecutor(messageExecutor));
    }

    @Override
    public void send_CmdUci() {
        service.open();
        sendRequestWaitResponse(new WaitRspUciOk(this), new CmdUci());
    }

    @Override
    public void send_CmdIsReady() {
        sendRequestWaitResponse(new WaitRspReadyOk(this), new CmdIsReady());
    }

    @Override
    public void send_CmdUciNewGame() {
        sendRequestNoWaitResponse(new CmdUciNewGame());
    }

    @Override
    public void send_CmdPosition(CmdPosition cmdPosition) {
        sendRequestNoWaitResponse(cmdPosition);
    }

    @Override
    public RspBestMove send_CmdGo(CmdGo cmdGo) {
        return (RspBestMove) sendRequestWaitResponse(new WaitRspBestMove(this), this.cmdGo == null ? cmdGo : this.cmdGo);
    }

    @Override
    public void send_CmdStop() {
        sendRequestNoWaitResponse(new CmdStop());
    }

    @Override
    public void send_CmdQuit() {
        sendRequestNoWaitResponse(new CmdQuit());
        service.close();
    }

    @Override
    public String getEngineName() {
        return engineName;
    }

    @Override
    public String getEngineAuthor() {
        return engineAuthor;
    }

    @Override
    public Controller overrideEngineName(String name) {
        this.engineName = name;
        return this;
    }

    @Override
    public Controller overrideCmdGo(CmdGo cmdGo) {
        this.cmdGo = cmdGo;
        return this;
    }

    public synchronized void sendRequestNoWaitResponse(UCIRequest request) {
        this.response = null;
        this.currentState = new NoWaitRsp();
        service.accept(request);
    }

    public synchronized UCIResponse sendRequestWaitResponse(UCIGui newState, UCIRequest request) {
        this.response = null;
        this.currentState = newState;
        service.accept(request);
        try {
            /**
             * Luego de service.accept(request), el mismo thread puede llamar a responseReceived y no bloquearse.
             * Por lo tanto esperamos solo si todavia no recibimos resuesta.
             */
            if (response == null) {
                wait(20000);
            }
            if (response == null) {
                logger.error("Engine {} has not provided any response after sending: {}", engineName, request);
                throw new RuntimeException("Perhaps engine has closed its output");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public synchronized void responseReceived(UCIResponse response) {
        this.currentState = new NoWaitRsp();
        this.response = response;
        notifyAll();
    }


}
