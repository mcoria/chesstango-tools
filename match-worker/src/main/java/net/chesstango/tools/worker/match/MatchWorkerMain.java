package net.chesstango.tools.worker.match;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class MatchWorkerMain implements Runnable {

    public static void main(String[] args) {
        String rabbitHost = System.getenv("RABBIT_HOST");
        String enginesCatalog = System.getenv("ENGINE_CATALOG");

        new MatchWorkerMain(rabbitHost, enginesCatalog).run();
    }

    private final String rabbitHost;
    private final String enginesCatalog;

    public MatchWorkerMain(String rabbitHost, String enginesCatalog) {
        if (rabbitHost == null || enginesCatalog == null) {
            throw new IllegalArgumentException("rabbitHost and enginesCatalog must be provided");
        }
        this.rabbitHost = rabbitHost;
        this.enginesCatalog = enginesCatalog;
    }

    @Override
    public void run() {
        log.info("To exit press CTRL+C");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setSharedExecutor(executorService);

            log.info("Connecting to RabbitMQ");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel();) {

                channel.basicQos(1);

                log.info("Connected to RabbitMQ");

                try (ControllerProvider controllerProvider = ControllerProvider.create(enginesCatalog);
                     RequestConsumer requestConsumer = new RequestConsumer(channel)) {
                    ResponseProducer responseProducer = new ResponseProducer(channel);

                    log.info("Connected to RabbitMQ");

                    MatchWorker matchWorker = new MatchWorker(controllerProvider);

                    requestConsumer.setupQueueConsumer(matchWorker, responseProducer::publish);

                    log.info("Waiting for MatchRequest");

                    Thread.sleep(Long.MAX_VALUE);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Done");
    }
}
