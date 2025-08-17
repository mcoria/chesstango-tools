package net.chesstango.tools.worker.match;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.chesstango.gardel.fen.FEN;
import net.chesstango.uci.arena.MatchResult;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Mauricio Coria
 */
@Setter
@Getter
@Accessors(chain = true)
public class MatchResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String whiteEngineName;
    private String blackEngineName;
    private FEN fen;
    private MatchResult matchResult;
}
