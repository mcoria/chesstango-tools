package net.chesstango.uci.arena.gui;

import net.chesstango.uci.engine.proxy.UciProxy;

/**
 * @author Mauricio Coria
 */
public class EngineControllerProxy extends EngineControllerImp {
    private final UciProxy uciProxy;

    public EngineControllerProxy(UciProxy uciProxy) {
        super(uciProxy);
        this.uciProxy = uciProxy;
    }

    @Override
    public void accept(ServiceVisitor serviceVisitor) {
        serviceVisitor.visit(uciProxy);
    }
}
