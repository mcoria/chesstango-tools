package net.chesstango.tools.worker.epd;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.chesstango.tools.worker.epd.result.EpdSearchResult;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Mauricio Coria
 */
@Accessors(chain = true)
@Getter
@Setter
public class EpdSearchResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<EpdSearchResult> epdSearchResults;
}
