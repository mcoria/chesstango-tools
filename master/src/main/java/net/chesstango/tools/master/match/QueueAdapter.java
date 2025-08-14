package net.chesstango.tools.master.match;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.match.MatchRequest;
import net.chesstango.tools.worker.match.MatchResponse;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class QueueAdapter implements AutoCloseable {
    private final static String RPC_QUEUE_NAME = "matches";

    private final Connection connection;
    private final Channel channel;
    private final String replyQueueName;
    private String corrId;

    static QueueAdapter open(ConnectionFactory factory) throws IOException, TimeoutException {
        return new QueueAdapter(factory);
    }

    QueueAdapter(ConnectionFactory factory) throws IOException, TimeoutException {
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);
        replyQueueName = channel.queueDeclare().getQueue();
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }

    public void publish(MatchRequest matchRequest) throws IOException {
        corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        byte[] message = encodeRequest(matchRequest);

        channel.basicPublish("", RPC_QUEUE_NAME, props, message);
    }

    public void waitResponse() throws IOException, ExecutionException, InterruptedException {
        final CompletableFuture<MatchResponse> responseFuture = new CompletableFuture<>();

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            log.info("Response received");
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                MatchResponse response = decodeResponse(delivery.getBody());
                responseFuture.complete(response);
            } else {
                System.out.println("Received message with wrong correlation id: " + delivery.getProperties().getCorrelationId());
            }
        }, consumerTag -> {
        });


        log.info("Waiting sesponse");

        MatchResponse response = responseFuture.get();

        log.info("Response: {}", response);

        channel.basicCancel(ctag);
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
