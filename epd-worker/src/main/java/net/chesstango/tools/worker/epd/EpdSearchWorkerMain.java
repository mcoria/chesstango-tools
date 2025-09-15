package net.chesstango.tools.worker.epd;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class EpdSearchWorkerMain implements Runnable {
    private final String rabbitHost;

    public EpdSearchWorkerMain(String rabbitHost) {
        if (rabbitHost == null) {
            throw new IllegalArgumentException("rabbitHost and enginesCatalog must be provided");
        }
        this.rabbitHost = rabbitHost;
    }

    public static void main(String[] args) throws Exception {
        String rabbitHost = System.getenv("RABBIT_HOST");

        new EpdSearchWorkerMain(rabbitHost).run();
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
            try (EpdSearchConsumer epdSearchConsumer = EpdSearchConsumer.open(factory)) {

                log.info("Connected to RabbitMQ");

                EpdSearchWorker epdSearchWorker = new EpdSearchWorker();

                CountDownLatch countDownLatch = new CountDownLatch(500);

                epdSearchConsumer.setupQueueConsumer(epdSearchWorker, () -> countDownLatch.getCount() == 1, countDownLatch::countDown);

                log.info("Waiting for EpdSearchRequest");

                countDownLatch.await();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Done");
    }
}
