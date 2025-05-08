module net.chesstango.uci.arena {
    exports net.chesstango.uci.arena;
    exports net.chesstango.uci.arena.listeners;
    exports net.chesstango.uci.arena.matchtypes;
    exports net.chesstango.uci.arena.gui;

    requires net.chesstango.goyeneche;
    requires net.chesstango.uci.engine;
    requires net.chesstango.evaluation;
    requires net.chesstango.board;
    requires net.chesstango.search;
    requires net.chesstango.mbeans;
    requires net.chesstango.engine;

    requires org.apache.commons.pool2;
    requires java.management;
    requires org.slf4j;

    requires static lombok;
    requires net.chesstango.uci.proxy;
    requires net.chesstango.uci.gui;
    requires net.chesstango.gardel;
}