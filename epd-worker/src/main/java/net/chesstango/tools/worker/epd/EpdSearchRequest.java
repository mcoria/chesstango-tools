package net.chesstango.tools.worker.epd;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.chesstango.gardel.epd.EPD;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Mauricio Coria
 */
@Accessors(chain = true)
@Getter
@Setter
public class EpdSearchRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String searchId;
    private List<EPD> epdList;
    private int depth;
    private int timeOut;
}
