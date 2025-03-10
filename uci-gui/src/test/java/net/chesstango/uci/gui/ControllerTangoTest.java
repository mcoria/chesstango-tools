package net.chesstango.uci.gui;


import net.chesstango.uci.engine.UciTango;
import net.chesstango.uci.protocol.requests.ReqPosition;
import net.chesstango.uci.protocol.requests.go.ReqGoDepth;
import net.chesstango.uci.protocol.responses.RspBestMove;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Mauricio Coria
 */
public class ControllerTangoTest {

    @Test
    public void test_Tango() {
        UciTango service = new UciTango();

        ControllerAbstract client = new ControllerTango(service);

        client.open();

        client.send_ReqUci();

        assertEquals("Mauricio Coria", client.getEngineAuthor());
        assertTrue(client.getEngineName().startsWith("Tango"));

        client.send_ReqIsReady();

        client.send_ReqUciNewGame();

        client.send_ReqPosition(new ReqPosition());

        RspBestMove bestmove = client.send_ReqGo(new ReqGoDepth().setDepth(1));

        assertNotNull(bestmove);

        client.send_ReqQuit();

        client.close();
    }


}
