package network;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable {

    private final Map<Router, Router> table;

    public RoutingTable(Topology topology, Router root) {
	table = new HashMap<>();
	SpanningTree spanningTree = new SpanningTree(root);

	table.put(root, root);
	for (Router router : topology) {
	    table.put(router, spanningTree.getNextHop(router));
	}
    }

    public Router getNext(Router destination) {
	return table.get(destination);
    }
}
