package net.chesstango.tools.master.match;

import com.rabbitmq.client.ConnectionFactory;
import net.chesstango.gardel.fen.FENParser;
import net.chesstango.tools.worker.match.MatchRequest;
import net.chesstango.uci.arena.matchtypes.MatchByDepth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
public class MatchMasterMain {

    public static void main(String[] args) throws Exception {

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setSharedExecutor(executorService);

            try (MatchProducer matchProducer = MatchProducer.open(factory)) {

                matchProducer.setupCallback(new MatchResponseCallback());

                MatchRequest matchRequest = new MatchRequest()
                        .setWhiteEngine("class:DefaultTango")
                        .setBlackEngine("file:Spike")
                        .setFen(FENParser.INITIAL_FEN)
                        .setMatchType(new MatchByDepth(2));

                matchProducer.publish(matchRequest);

                Thread.sleep(Long.MAX_VALUE);
            }
        }
    }
}
