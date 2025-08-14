package net.chesstango.tools.master.match;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.chesstango.gardel.fen.FENParser;
import net.chesstango.tools.worker.match.MatchRequest;
import net.chesstango.tools.worker.match.MatchResponse;
import net.chesstango.uci.arena.matchtypes.MatchByDepth;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
public class MatchMasterMain {
    private final static String RPC_QUEUE_NAME = "matches";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (ExecutorService executorService = Executors.newSingleThreadExecutor();
             Connection connection = factory.newConnection(executorService);
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            MatchRequest matchRequest = new MatchRequest()
                    .setWhiteEngine("class:net.chesstango.tools.worker.match.factories.DefaultTango")
                    .setBlackEngine("file:Spike")
                    .setFen(FENParser.INITIAL_FEN)
                    .setMatchType(new MatchByDepth(2));


            final String corrId = UUID.randomUUID().toString();

            String replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            byte[] message = encodeRequest(matchRequest);

            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicPublish("", RPC_QUEUE_NAME, props, message);

            final CompletableFuture<MatchResponse> responseFuture = new CompletableFuture<>();

            String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    MatchResponse response = decodeResponse(delivery.getBody());
                    responseFuture.complete(response);
                }
            }, consumerTag -> {
            });

            MatchResponse response = responseFuture.get();

            System.out.println("Response: " + response);

            channel.basicCancel(ctag);
        }
    }

    private static byte[] encodeRequest(MatchRequest matchRequest) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            oos.writeObject(matchRequest);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static MatchResponse decodeResponse(byte[] request) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(request);
             ObjectInputStream ois = new ObjectInputStream(bis);) {
            return (MatchResponse) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
