package net.chesstango.uci.proxy;

import net.chesstango.uci.protocol.UciService;
import net.chesstango.uci.protocol.stream.UCIActiveStreamReader;
import net.chesstango.uci.protocol.stream.UCIInputStreamAdapter;
import net.chesstango.uci.protocol.stream.UCIOutputStreamToStringAdapter;
import net.chesstango.uci.protocol.stream.strings.StringConsumer;
import net.chesstango.uci.protocol.stream.strings.StringSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Mauricio Coria
 */
public class UciMain implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(UciMain.class);
    private final UciService service;
    private final InputStream in;

    private final PrintStream out;

    private final UCIActiveStreamReader pipe;
    private volatile boolean isRunning;

    public static void main(String[] args) {
        UciMain uciMain = new UciMain(new UciProxy(ProxyConfigLoader.loadEngineConfig("Spike")), System.in, System.out);
        uciMain.run();
    }

    public UciMain(UciService service, InputStream in, PrintStream out) {
        this.service = service;
        this.in = in;
        this.out = out;
        this.pipe = new UCIActiveStreamReader();

    }

    @Override
    public void run() {
        this.service.setResponseOutputStream(new UCIOutputStreamToStringAdapter(new StringConsumer(new OutputStreamWriter(out))));
        this.pipe.setInputStream(new UCIInputStreamAdapter(new StringSupplier(new InputStreamReader(in))));
        this.pipe.setOutputStream(this.service);

        try {
            service.open();

            isRunning = true;

            pipe.run();

            isRunning = false;

            service.close();
        } catch (RuntimeException e) {
            logger.error("Error:", e);
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("Error:", e);
            }
            out.close();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
