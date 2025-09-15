module net.chesstango.tools.worker.epd {
    exports net.chesstango.tools.worker.epd;
    exports net.chesstango.tools.worker.epd.result;


    requires net.chesstango.board;
    requires net.chesstango.gardel;
    requires net.chesstango.search;
    requires net.chesstango.evaluation;

    requires org.slf4j;
    requires com.rabbitmq.client;
    requires static lombok;
}