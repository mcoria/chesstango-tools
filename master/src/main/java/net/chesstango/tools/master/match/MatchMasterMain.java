package net.chesstango.tools.master.match;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.chesstango.gardel.fen.FENParser;
import net.chesstango.tools.worker.match.MatchRequest;
import net.chesstango.uci.arena.matchtypes.MatchByDepth;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author Mauricio Coria
 */
public class MatchMasterMain {
    private final static String QUEUE_NAME = "matches";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            MatchRequest matchRequest = new MatchRequest()
                    .setWhiteEngineName("Tango")
                    .setBlackEngineName("Spike")
                    .setFen(FENParser.INITIAL_FEN)
                    .setMatchType(new MatchByDepth(2));

            byte[] message = null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos);) {
                oos.writeObject(matchRequest);
                oos.flush();
                message = bos.toByteArray();
            }

            channel.basicPublish("", QUEUE_NAME, null, message);
        }
    }
}
