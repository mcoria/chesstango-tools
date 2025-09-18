package net.chesstango.tools.worker.epd;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.chesstango.tools.worker.epd.EpdSearchRequest.EPD_REQUESTS_QUEUE_NAME;
import static net.chesstango.tools.worker.epd.EpdSearchResponse.EPD_RESPONSES_QUEUE_NAME;

/**
 * @author Mauricio Coria
 */
@Slf4j
class EpdSearchConsumer implements AutoCloseable{

    private final Channel channel;
    private String cTag;

    public EpdSearchConsumer(Channel channel) {
        this.channel = channel;
    }


    @Override
    public void close() throws Exception {
        channel.basicCancel(cTag);
    }


    public void setupQueueConsumer(Function<EpdSearchRequest, EpdSearchResponse> fnSearch, Consumer<EpdSearchResponse> fnConsumerResponse) {
        try {
            cTag = channel.basicConsume(EPD_REQUESTS_QUEUE_NAME, false, (consumerTag, delivery) -> {

                EpdSearchRequest request = EpdSearchRequest.decodeRequest(delivery.getBody());

                EpdSearchResponse response = fnSearch.apply(request);

                fnConsumerResponse.accept(response);

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }, consumerTag -> {
                log.info("Queue consumer cancelled {}", cTag);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
