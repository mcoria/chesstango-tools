module net.chesstango.reports {
    exports net.chesstango.reports.evaluation;
    exports net.chesstango.reports.nodes;

    requires net.chesstango.search;
    requires net.chesstango.board;
    requires net.chesstango.evaluation;

    requires static lombok;
}