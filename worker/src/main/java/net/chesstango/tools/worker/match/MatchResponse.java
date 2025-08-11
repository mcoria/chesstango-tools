package net.chesstango.tools.worker.match;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.chesstango.uci.arena.MatchResult;

/**
 * @author Mauricio Coria
 */
@Setter
@Getter
@Accessors(chain = true)
public class MatchResponse {
    private String whiteEngineName;
    private String blackEngineName;
    private String fen;
    private MatchResult matchResult;
}
