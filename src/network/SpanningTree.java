package network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SpanningTree {

    private final SpanningTreeNode root;

    public SpanningTree(Router router) {
	root = new SpanningTreeNode(router);

	Queue<SpanningTreeNode> queue = new LinkedList<>();
	Set<Router> visitedSet = new HashSet<>();
	queue.add(root);
	visitedSet.add(router);

	while (!queue.isEmpty()) {
	    SpanningTreeNode node = queue.remove();

	    for (Router connection : node.router.getConnections()) {
		if (!visitedSet.contains(connection)) {
		    SpanningTreeNode child = new SpanningTreeNode(connection);
		    visitedSet.add(connection);

		    node.addChild(child);
		    queue.add(child);
		}
	    }
	}
    }

    public Router getNextHop(Router router) {
	if (router == root.router || router.isConnectedTo(root.router)) {
	    return router;
	}

	// Locate the router using DFS
	Deque<SpanningTreeNode> nodes = new ArrayDeque<>();
	Deque<SpanningTreeNode> path = new ArrayDeque<>();
	nodes.push(root);

	while (!nodes.isEmpty()) {
	    SpanningTreeNode node = nodes.pop();
	    path.push(node);
	    if (node.router == router) {
		while (path.size() > 2)
		    path.pop();
		return path.pop().router;
	    } else {
		if (node.hasChildren()) {
		    for (SpanningTreeNode child : node.children) {
			nodes.push(child);
		    }
		} else {
		    path.pop(); // backtrack
		}
	    }
	}

	return null;
    }

    private class SpanningTreeNode {
	public final Router router;
	public final List<SpanningTreeNode> children;

	public SpanningTreeNode(Router router) {
	    this.router = router;
	    children = new ArrayList<>();
	}

	public void addChild(SpanningTreeNode child) {
	    children.add(child);
	}

	public boolean hasChildren() {
	    return !children.isEmpty();
	}
    }
}
