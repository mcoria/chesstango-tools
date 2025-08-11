package net.chesstango.tools.worker.match;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mauricio Coria
 */
public class MatchWorkerMain {


    public static void main(String[] args) throws Exception {
        System.out.println("[*] Waiting for messages. To exit press CTRL+C");

        MatchWorker matchWorker = new MatchWorker(ControllerProvider.create("C:\\java\\projects\\chess\\chess-utils\\engines\\catalog"));

        try (ExecutorService executorService = Executors.newSingleThreadExecutor();
             QueueConsumer queueConsumer = QueueConsumer.open(executorService)) {

            queueConsumer.consumeMessages(matchRequest -> {
                MatchResponse response = matchWorker.run(matchRequest);

                System.out.println(response);
            });

            Thread.sleep(Long.MAX_VALUE);
        }

        System.out.println("[x] Done");
    }
}
