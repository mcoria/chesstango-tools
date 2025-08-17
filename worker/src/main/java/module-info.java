module net.chesstango.tools.worker {
    exports net.chesstango.tools.worker.match;

    requires net.chesstango.uci.arena;
    requires net.chesstango.uci.gui;

    requires org.slf4j;
    requires static lombok;
    requires com.rabbitmq.client;
    requires net.chesstango.gardel;
}