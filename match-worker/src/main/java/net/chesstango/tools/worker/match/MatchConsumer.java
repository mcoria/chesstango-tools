package net.chesstango.tools.worker.match;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Mauricio Coria
 */
@Slf4j
class MatchConsumer implements AutoCloseable {
    private final static String RPC_QUEUE_NAME = "matches";

    private final Connection connection;
    private final Channel channel;

    private String cTag;

    static MatchConsumer open(ConnectionFactory factory) throws IOException, TimeoutException {
        return new MatchConsumer(factory);
    }

    MatchConsumer(ConnectionFactory factory) throws IOException, TimeoutException {
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


    public void setupQueueConsumer(Function<MatchRequest, MatchResponse> matchFn, Supplier<Boolean> cancelQueueConsumer, Runnable onConsumed) {
        try {
            cTag = channel.basicConsume(RPC_QUEUE_NAME, false, (consumerTag, delivery) -> {

                if (cancelQueueConsumer.get()) {
                    channel.basicCancel(cTag);
                }

                MatchRequest request = decodeRequest(delivery.getBody());

                MatchResponse response = matchFn.apply(request);

                byte[] encodedResponse = encodeResponse(response);

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .build();

                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, encodedResponse);

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                onConsumed.run();
            }, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
