package net.chesstango.tools.worker.match;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.chesstango.uci.arena.matchtypes.MatchType;

/**
 * @author Mauricio Coria
 */
@Setter
@Getter
@Accessors(chain = true)
public class MatchRequest {
    private String whiteEngineName;
    private String blackEngineName;
    private String fen;
    private MatchType matchType;
}
