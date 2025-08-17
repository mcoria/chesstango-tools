package net.chesstango.tools.master.match;

import lombok.extern.slf4j.Slf4j;
import net.chesstango.tools.worker.match.MatchResponse;

import java.util.function.Consumer;

/**
 * @author Mauricio Coria
 */

@Slf4j
public class MatchResponseCallback implements Consumer<MatchResponse> {
    @Override
    public void accept(MatchResponse matchResponse) {
        log.info("Response received");
    }
}
