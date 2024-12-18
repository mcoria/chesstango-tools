package net.chesstango.uci.arena.gui;


import net.chesstango.uci.engine.proxy.ProxyConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Mauricio Coria
 */
public class ProxyConfigTest {

    @Test
    public void testReadConfigs() {
        List<ProxyConfig> configs = ProxyConfigLoader.loadFromFile();
        assertFalse(configs.isEmpty());
    }
}
