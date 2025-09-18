package net.chesstango.tools.epd;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.epd.EpdSearchRequest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static net.chesstango.tools.worker.epd.EpdSearchRequest.EPD_REQUESTS_QUEUE_NAME;


/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchProducer implements AutoCloseable {
    private final Connection connection;
    private final Channel channel;

    public static EpdSearchProducer open(ConnectionFactory factory) throws IOException, TimeoutException {
        return new EpdSearchProducer(factory);
    }

    EpdSearchProducer(ConnectionFactory factory) throws IOException, TimeoutException {
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.queueDeclare(EPD_REQUESTS_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(EPD_REQUESTS_QUEUE_NAME);
        channel.basicQos(1);
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }

    public void publish(EpdSearchRequest epdSearchRequest) {
        try {
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .build();
            byte[] message = epdSearchRequest.encodeRequest();
            channel.basicPublish("", EPD_REQUESTS_QUEUE_NAME, props, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
