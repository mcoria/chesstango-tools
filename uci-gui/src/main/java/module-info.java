module net.chesstango.uci.gui {
    exports net.chesstango.uci.gui;
    requires net.chesstango.uci.protocol;
    requires org.slf4j;

    requires static lombok;
    requires net.chesstango.uci.engine;
    requires net.chesstango.uci.proxy;
    requires net.chesstango.engine;
    requires net.chesstango.search;
    requires net.chesstango.evaluation;
}