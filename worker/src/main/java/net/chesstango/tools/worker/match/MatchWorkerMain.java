package net.chesstango.tools.worker.match;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class MatchWorkerMain implements Runnable {

    private final String rabbitHost;
    private final String enginesCatalog;

    public MatchWorkerMain(String rabbitHost, String enginesCatalog) {
        if (rabbitHost == null || enginesCatalog == null) {
            throw new IllegalArgumentException("rabbitHost and enginesCatalog must be provided");
        }
        this.rabbitHost = rabbitHost;
        this.enginesCatalog = enginesCatalog;
    }

    public static void main(String[] args) throws Exception {
        String rabbitHost = System.getenv("RABBIT_HOST");
        String enginesCatalog = System.getenv("ENGINE_CATALOG");

        new MatchWorkerMain(rabbitHost, enginesCatalog).run();
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
            try (ControllerProvider controllerProvider = ControllerProvider.create(enginesCatalog);
                 MatchConsumer matchConsumer = MatchConsumer.open(factory)) {

                log.info("Connected to RabbitMQ");

                MatchWorker matchWorker = new MatchWorker(controllerProvider);

                CountDownLatch countDownLatch = new CountDownLatch(10);

                matchConsumer.setupQueueConsumer(matchWorker, () -> {
                    if (countDownLatch.getCount() == 1) {
                        matchConsumer.endQueueConsumer();
                    }
                    countDownLatch.countDown();
                });

                log.info("Waiting for MatchRequest");

                countDownLatch.await();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Done");
    }
}
