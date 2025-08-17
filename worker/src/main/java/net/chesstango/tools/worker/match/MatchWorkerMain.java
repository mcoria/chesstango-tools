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

    private final String enginesCatalog;

    public MatchWorkerMain(String enginesCatalog) {
        this.enginesCatalog = enginesCatalog;
    }

    public static void main(String[] args) throws Exception {
        String enginesCatalog = System.getenv("ENGINE_CATALOG");

        new MatchWorkerMain(enginesCatalog).run();
    }

    @Override
    public void run() {
        log.info("To exit press CTRL+C");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setSharedExecutor(executorService);

            log.info("Connecting to RabbitMQ");
            try (ControllerProvider controllerProvider = ControllerProvider.create(enginesCatalog);
                 MatchConsumer matchConsumer = MatchConsumer.open(factory)) {

                log.info("Connected to RabbitMQ");

                MatchWorker matchWorker = new MatchWorker(controllerProvider);

                CountDownLatch countDownLatch = new CountDownLatch(5);

                matchConsumer.setupQueueConsumer(matchWorker, countDownLatch::countDown);

                log.info("Waiting for MatchRequest");

                countDownLatch.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Done");
    }
}
