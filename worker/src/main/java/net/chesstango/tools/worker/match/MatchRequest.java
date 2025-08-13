package net.chesstango.tools.worker.match;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.chesstango.uci.arena.matchtypes.MatchType;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Mauricio Coria
 */
@Setter
@Getter
@Accessors(chain = true)
public class MatchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String whiteEngine;
    private String blackEngine;
    private String fen;
    private MatchType matchType;
}
