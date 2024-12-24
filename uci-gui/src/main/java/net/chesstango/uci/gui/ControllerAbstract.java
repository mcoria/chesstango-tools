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

    @Setter
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
        this.currentState = new NoWaitRsp();
    }

    @Override
    public void send_CmdUci() {
        service.open();
		currentState = new WaitRspUciOk(this);
        sendRequest(new CmdUci(), true);
    }

    @Override
    public void send_CmdIsReady() {
        currentState = new WaitRspReadyOk(this);
        sendRequest(new CmdIsReady(), true);
    }

    @Override
    public void send_CmdUciNewGame() {
        sendRequest(new CmdUciNewGame(), false);
    }

    @Override
    public void send_CmdPosition(CmdPosition cmdPosition) {
        sendRequest(cmdPosition, false);
    }

    @Override
    public RspBestMove send_CmdGo(CmdGo cmdGo) {
        currentState = new WaitRspBestMove(this);
        return (RspBestMove) sendRequest(this.cmdGo == null ? cmdGo : this.cmdGo, true);
    }

    @Override
    public void send_CmdStop() {
        sendRequest(new CmdStop(), false);
    }

    @Override
    public void send_CmdQuit() {
        sendRequest(new CmdQuit(), false);
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

    public synchronized UCIResponse sendRequest(UCIRequest request, boolean waitResponse) {
        this.response = null;
        service.accept(request);
        if (waitResponse) {
            try {
                int waitingCounter = 0;
                while (response == null && waitingCounter < 20 ) {
                    wait(1000);
                    waitingCounter++;
                }
                if (response == null) {
                    logger.error("Engine {} has not provided any response after sending: {}", engineName, request);
                    throw new RuntimeException("Perhaps engine has closed its output");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    public synchronized void responseReceived(UCIResponse response) {
        this.response = response;
        notifyAll();
    }


}
