package net.chesstango.tools.worker.match;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
@Slf4j
public class MatchWorkerMain {


    public static void main(String[] args) throws Exception {
        log.info("[*] Waiting for MatchRequest. To exit press CTRL+C");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setSharedExecutor(executorService);

            try (ControllerProvider controllerProvider = ControllerProvider.create("C:\\java\\projects\\chess\\chess-utils\\engines\\catalog");
                 QueueAdapter queueAdapter = QueueAdapter.open(factory)) {

                MatchWorker matchWorker = new MatchWorker(controllerProvider);

                queueAdapter.setupQueueConsumer(matchWorker);

                Thread.sleep(Long.MAX_VALUE);
            }
        }

        log.info("[x] Done");
    }


}
