package net.chesstango.tools.epd.master;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.epd.EpdSearchRequest;
import net.chesstango.tools.worker.epd.EpdSearchResponse;

import java.io.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchProducer implements AutoCloseable {
    private final static String RPC_QUEUE_NAME = "epd";

    private final Connection connection;
    private final Channel channel;
    private final String replyQueueName;
    private String ctag;

    public static EpdSearchProducer open(ConnectionFactory factory) throws IOException, TimeoutException {
        return new EpdSearchProducer(factory);
    }

    EpdSearchProducer(ConnectionFactory factory) throws IOException, TimeoutException {
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);
        replyQueueName = channel.queueDeclare().getQueue();
    }

    @Override
    public void close() throws Exception {
        if (ctag != null) {
            channel.basicCancel(ctag);
        }
        channel.close();
        connection.close();
    }

    public void publish(EpdSearchRequest epdSearchRequest) {
        try {
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .replyTo(replyQueueName)
                    .build();

            byte[] message = encodeRequest(epdSearchRequest);

            channel.basicPublish("", RPC_QUEUE_NAME, props, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setupCallback(Consumer<EpdSearchResponse> matchResponseConsumer) {
        try {
            ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {

                EpdSearchResponse response = decodeResponse(delivery.getBody());

                matchResponseConsumer.accept(response);

            }, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static byte[] encodeRequest(EpdSearchRequest epdSearchRequest) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            oos.writeObject(epdSearchRequest);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static EpdSearchResponse decodeResponse(byte[] request) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(request);
             ObjectInputStream ois = new ObjectInputStream(bis);) {
            return (EpdSearchResponse) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
