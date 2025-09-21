package net.chesstango.tools.arena;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.match.MatchRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static net.chesstango.tools.worker.match.MatchRequest.MATCH_REQUESTS_QUEUE_NAME;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class MatchRequestProducer implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;

    public static MatchRequestProducer open(ConnectionFactory factory) throws IOException, TimeoutException {
        return new MatchRequestProducer(factory);
    }

    MatchRequestProducer(ConnectionFactory factory) throws IOException, TimeoutException {
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        this.channel.queueDeclare(MATCH_REQUESTS_QUEUE_NAME, false, false, false, null);
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }

    public void publish(MatchRequest matchRequest) {
        try {
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .build();

            byte[] message = matchRequest.encodeRequest();

            channel.basicPublish("", MATCH_REQUESTS_QUEUE_NAME, props, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
