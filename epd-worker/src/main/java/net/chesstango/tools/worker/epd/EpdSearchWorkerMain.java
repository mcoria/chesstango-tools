package net.chesstango.tools.worker.epd;

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
public class EpdSearchWorkerMain implements Runnable {

    public static void main(String[] args) throws Exception {
        String rabbitHost = System.getenv("RABBIT_HOST");

        new EpdSearchWorkerMain(rabbitHost).run();
    }

    private final String rabbitHost;

    public EpdSearchWorkerMain(String rabbitHost) {
        if (rabbitHost == null) {
            throw new IllegalArgumentException("rabbitHost and enginesCatalog must be provided");
        }
        this.rabbitHost = rabbitHost;
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

                try (EpdSearchConsumer epdSearchConsumer = new EpdSearchConsumer(channel)) {

                    EpdSearchWorker epdSearchWorker = new EpdSearchWorker();

                    EpdSearchProducer epdSearchProducer = new EpdSearchProducer(channel);

                    epdSearchConsumer.setupQueueConsumer(epdSearchWorker, epdSearchProducer::publish);

                    log.info("Waiting for EpdSearchRequest");

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
