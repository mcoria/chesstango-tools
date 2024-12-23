package net.chesstango.uci.gui;

import net.chesstango.uci.protocol.requests.CmdPosition;
import net.chesstango.uci.protocol.requests.go.CmdGoDepth;
import net.chesstango.uci.protocol.responses.RspBestMove;
import net.chesstango.uci.proxy.ProxyConfigLoader;
import net.chesstango.uci.proxy.UciProxy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;



/**
 * @author Mauricio Coria
 */
public class ControllerProxyIntegrationTest {

    @Test
    public void test_Proxy() {
        UciProxy engine = new UciProxy(ProxyConfigLoader.loadEngineConfig("Spike"));

        ControllerAbstract client = new ControllerProxy(engine);

        client.send_CmdUci();

        //assertEquals("Ralf Schäfer und Volker Böhm", client.getEngineAuthor());
        assertEquals("Spike 1.4", client.getEngineName());

        client.send_CmdIsReady();

        client.send_CmdUciNewGame();

        client.send_CmdPosition(new CmdPosition());

        RspBestMove bestmove = client.send_CmdGo(new CmdGoDepth()
                .setDepth(1));

        assertNotNull(bestmove);

        client.send_CmdQuit();
    }


}
