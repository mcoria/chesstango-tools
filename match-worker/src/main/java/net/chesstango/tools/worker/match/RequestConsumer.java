package net.chesstango.tools.worker.match;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.chesstango.tools.worker.match.MatchRequest.MATCH_REQUESTS_QUEUE_NAME;

/**
 * @author Mauricio Coria
 */
@Slf4j
class RequestConsumer implements AutoCloseable {

    private final Channel channel;
    private String cTag;

    public RequestConsumer(Channel channel) {
        this.channel = channel;
    }


    @Override
    public void close() throws Exception {
        channel.basicCancel(cTag);
    }


    public void setupQueueConsumer(Function<MatchRequest, MatchResponse> matchFn, Consumer<MatchResponse> fnConsumerResponse) {
        try {
            cTag = channel.basicConsume(MATCH_REQUESTS_QUEUE_NAME, true, (consumerTag, delivery) -> {

                MatchRequest request = MatchRequest.decodeRequest(delivery.getBody());

                MatchResponse response = matchFn.apply(request);

                fnConsumerResponse.accept(response);

            }, consumerTag -> {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

