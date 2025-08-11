package net.chesstango.tools.worker.match;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * @author Mauricio Coria
 */
@Slf4j
class QueueConsumer implements AutoCloseable {

    private final static String QUEUE_NAME = "matches";

    private final Connection connection;
    private final Channel channel;

    QueueConsumer(Connection connection, Channel channel) throws IOException {
        this.connection = connection;
        this.channel = channel;
    }


    static QueueConsumer open(ExecutorService executorService) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        Connection connection = factory.newConnection(executorService);

        Channel channel = connection.createChannel();

        channel.basicQos(1);

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        return new QueueConsumer(connection, channel);
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }


    public void consumeMessages(Consumer<MatchRequest> matchRequestConsumer) throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            byte[] serializedData = delivery.getBody();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
                 ObjectInputStream ois = new ObjectInputStream(bis);) {
                MatchRequest request = (MatchRequest) ois.readObject();
                matchRequestConsumer.accept(request);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }
}
