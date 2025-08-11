package net.chesstango.tools.worker.match;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
public class MatchWorkerMain {


    public static void main(String[] args) throws Exception {
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        try (ExecutorService executorService = Executors.newSingleThreadExecutor();
             QueueConsumer queueConsumer = QueueConsumer.open(executorService)) {


            queueConsumer.consumeMessages(System.out::println);

            Thread.sleep(Long.MAX_VALUE);
        }

        System.out.println(" [x] Done");
    }
}
