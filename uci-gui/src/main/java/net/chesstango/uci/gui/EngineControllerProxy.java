package net.chesstango.uci.gui;

import net.chesstango.uci.proxy.UciProxy;

/**
 * @author Mauricio Coria
 */
public class EngineControllerProxy extends EngineControllerAbstract {
    private final UciProxy uciProxy;

    public EngineControllerProxy(UciProxy uciProxy) {
        super(uciProxy);
        this.uciProxy = uciProxy;
    }

    @Override
    public void accept(EngineControllerVisitor engineControllerVisitor) {
        engineControllerVisitor.visit(uciProxy);
    }
}
