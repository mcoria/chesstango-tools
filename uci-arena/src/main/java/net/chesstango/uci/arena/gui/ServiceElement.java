package net.chesstango.uci.arena.gui;

/**
 * @author Mauricio Coria
 */
public interface ServiceElement {
    void accept(ServiceVisitor serviceVisitor);
}
