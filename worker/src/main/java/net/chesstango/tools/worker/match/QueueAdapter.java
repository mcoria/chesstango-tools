package net.chesstango.tools.worker.match;


import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * @author Mauricio Coria
 */
@Slf4j
class QueueAdapter implements AutoCloseable {
    private final static String RPC_QUEUE_NAME = "matches";

    private final Connection connection;
    private final Channel channel;

    private Function<MatchRequest, MatchResponse> matchFn;

    static QueueAdapter open(ConnectionFactory factory) throws IOException, TimeoutException {
        return new QueueAdapter(factory);
    }

    QueueAdapter(ConnectionFactory factory) throws IOException, TimeoutException {
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.basicQos(1);
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }


    public void setupQueueConsumer(Function<MatchRequest, MatchResponse> matchFn) throws IOException {
        this.matchFn = matchFn;
        channel.basicConsume(RPC_QUEUE_NAME, false, this::handle, consumerTag -> {});
    }

    private void handle(String consumerTag, Delivery delivery) throws IOException{
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(delivery.getProperties().getCorrelationId())
                .build();

        MatchRequest request = decodeRequest(delivery.getBody());

        MatchResponse response = matchFn.apply(request);

        byte[] encodedResponse = encodeResponse(response);

        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, encodedResponse);

        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    }

    private byte[] encodeResponse(MatchResponse response) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            oos.writeObject(response);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MatchRequest decodeRequest(byte[] request) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(request);
             ObjectInputStream ois = new ObjectInputStream(bis);) {
            return (MatchRequest) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
