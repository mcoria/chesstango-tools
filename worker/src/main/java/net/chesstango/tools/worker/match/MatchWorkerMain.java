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
        log.info("[*] Waiting for messages. To exit press CTRL+C");


        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setSharedExecutor(executorService);

            try (ControllerProvider controllerProvider = ControllerProvider.create("C:\\java\\projects\\chess\\chess-utils\\engines\\catalog");
                 QueueConsumer queueConsumer = QueueConsumer.open(factory)) {

                MatchWorker matchWorker = new MatchWorker(controllerProvider);

                queueConsumer.consumeMessages(matchWorker);

                Thread.sleep(Long.MAX_VALUE);
            }
        }

        log.info("[x] Done");
    }
}
