package gui.graphics;

import gui.CenteredJTable;
import gui.models.PacketTableModel;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import network.Router;
import network.Topology;

import common.TopologyUpdateEvent;
import common.TopologyUpdateEvent.TopologyUpdate;

/**
 * A GUI element that contains a canvas where Routers (and their edges) will be
 * painted as nodes.
 * 
 * @author Jessica
 * @see RouterEdge
 * @see RouterNode
 */
@SuppressWarnings("serial")
public class TopologyCanvas extends JPanel implements Observer {

    private final TreeSet<RouterNode> nodes;
    private final Rectangle canvas;

    /**
     * Instantiate a blank canvas.
     */
    public TopologyCanvas() {
	nodes = new TreeSet<>();
	canvas = new Rectangle();

	MouseAdapter a = new TopologyCanvasMouseAdapter();
	addMouseMotionListener(a);
	addMouseListener(a);
    }

    /**
     * Add a new Router to the canvas.
     * 
     * @param router
     *            The new Router to insert.
     */
    private void addRouter(Router router) {
	RouterNode node = new RouterNode(router, 0, 0);
	nodes.add(node);
	repaint();
    }

    /**
     * Add a new Router to the canvas at a specified location.
     * 
     * @param router
     *            The new Router to insert.
     * @param x
     *            The x position of the Router.
     * @param y
     *            The y position of the Router.
     */
    public void addRouter(Router router, int x, int y) {
	RouterNode node = new RouterNode(router, x, y);
	nodes.add(node);
	repaint();
    }

    /**
     * Connect two RouterNodes on the canvas.
     * 
     * @param node1
     *            The first RouterNode to connect.
     * @param node2
     *            The second RouterNode to connect.
     */
    private void addEdge(RouterNode node1, RouterNode node2) {
	node1.addEdge(node2);
	repaint();
    }

    /**
     * Connect two routers on the canvas. This version of the method must locate
     * the RouterNode objects containing the given routers.
     * 
     * @param router1
     *            The Router of the first RouterNode to connect.
     * @param router2
     *            The Router of the second RouterNode to connect.
     */
    private void addEdge(Router router1, Router router2) {
	RouterNode node1 = getRouterNodeByRouter(router1);
	RouterNode node2 = getRouterNodeByRouter(router2);

	addEdge(node1, node2);
    }

    /**
     * Remove a Router from this canvas.
     * 
     * @param router
     *            The Router to remove.
     * @return True if the router is removed successfully, false if not found in
     *         the canvas.
     */
    private boolean removeRouter(Router router) {
	RouterNode node = getRouterNodeByRouter(router);
	if (node != null) {
	    // Remove edges
	    for (Router neighbor : router.getConnections()) {
		removeEdge(router, neighbor);
	    }

	    // Drop the router
	    nodes.remove(node);
	    remove(node); // Remove from view
	    repaint();

	    return true;
	}

	return false;
    }

    /**
     * Remove a connection between two Routers.
     * 
     * @param router1
     *            The first Router to disconnect.
     * @param router2
     *            The neighbouring Router to disconnect.
     */
    private void removeEdge(Router router1, Router router2) {
	RouterNode node1 = getRouterNodeByRouter(router1);
	RouterNode node2 = getRouterNodeByRouter(router2);

	node1.removeEdge(node2);
	repaint();
    }

    public void reset() {
	nodes.clear();
	repaint();
    }

    /**
     * Update the view of the canvas.
     */
    @Override
    public void paintComponent(Graphics g) {
	super.paintComponent(g);

	// Resize the canvas according to the lowest and right-most RouterNodes
	int newWidth = 0, newHeight = 0;
	for (RouterNode node : nodes) {
	    if (node.getX() > newWidth) {
		newWidth = node.getX();
	    }

	    if (node.getY() > newHeight) {
		newHeight = node.getY();
	    }
	}
	canvas.setBounds(0, 0, newWidth, newHeight);

	// Paint the edges first so they appear under the nodes
	Collection<RouterEdge> edges = new HashSet<>();
	for (RouterNode node : nodes) {
	    edges.addAll(node.getEdges());
	}

	for (RouterEdge edge : edges) {
	    edge.paintComponent(g);
	}

	// Paint the nodes
	for (RouterNode node : nodes) {
	    node.paintComponent(g);
	}
    }

    /**
     * Helper method for any methods that use Routers instead of RouterNodes as
     * parameters. Locates the RouterNode referred to by the parameter router.
     * 
     * @param router
     *            The Router to locate the RouterNode for.
     * @return The RouterNode containing the given router or null if it does not
     *         exist.
     */
    private RouterNode getRouterNodeByRouter(Router router) {
	for (RouterNode node : nodes) {
	    if (node.getRouter() == router) {
		return node;
	    }
	}

	return null;
    }

    /**
     * Get the RouterNode at the indicated point, if there is one.
     * 
     * @param point
     *            The coordinates to check.
     * @return The RouterNode at the point, or null if there was no RouterNode
     *         at that point.
     */
    private RouterNode getNodeAt(Point point) {
	// Iterate over nodes in reverse order
	for (RouterNode node : nodes.descendingSet()) {
	    if (node.contains(point)) {
		return node;
	    }
	}

	return null;
    }

    /**
     * Validate that the coordinates given by point are within the bounds of the
     * canvas. The canvas can stretch infinitely to the right and down.
     * 
     * @param point
     *            The coordinates to validate.
     * @return True if the point is in the canvas, false if out of bounds.
     */
    private boolean validateCoordinates(Point point) {
	return !(point.getX() < canvas.getMinX() || point.getY() < canvas
		.getMinY());
    }

    /**
     * This private inner class handles all mouse-related activities for the
     * canvas.
     * 
     * @author Jessica
     */
    private class TopologyCanvasMouseAdapter extends MouseAdapter {
	private RouterNode selectedNode = null;
	private int preX;
	private int preY;

	/**
	 * Handles a mouse-press.
	 * <p>
	 * For a double click, if a RouterNode was the target of the click, pops
	 * up a dialog with the packet information of that router.
	 * <p>
	 * For a single click, if a RouterNode was the target of the click,
	 * start click-and-drag movement.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	    // Check if a node was clicked on
	    selectedNode = getNodeAt(e.getPoint());

	    if (selectedNode != null) {
		// Double click
		if (e.getClickCount() == 2) {
		    Router router = selectedNode.getRouter();
		    if (router.getPackets().size() == 0) {
			JOptionPane.showMessageDialog(selectedNode,
				"No packets to show here.",
				"Packet Information - " + router.getName(),
				JOptionPane.PLAIN_MESSAGE);
		    } else {
			JTable packets = new CenteredJTable(
				new PacketTableModel(router.getPackets()));

			JOptionPane.showMessageDialog(selectedNode,
				new JScrollPane(packets),
				"Packet Information - " + router.getName(),
				JOptionPane.PLAIN_MESSAGE);
		    }
		    // Other clicks
		} else {
		    preX = selectedNode.getX() - e.getX();
		    preY = selectedNode.getY() - e.getY();
		    updateSelectedNodeLocation(e.getPoint());
		}
	    }
	}

	/**
	 * On a mouse drag, if a node was previously selected on a mouse press,
	 * update its location.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
	    if (selectedNode != null) {
		updateSelectedNodeLocation(e.getPoint());
	    }
	}

	/**
	 * On mouse release, if a node was previously selected on a mouse press,
	 * update its location and un-select the node.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	    if (selectedNode != null) {
		updateSelectedNodeLocation(e.getPoint());
		selectedNode = null;
	    }
	}

	/**
	 * Move the selected node to the location at coordinates given by point.
	 * 
	 * @param point
	 *            The coordinates of the new location.
	 */
	private void updateSelectedNodeLocation(Point point) {
	    if (validateCoordinates(point)) {
		Point newPosition = new Point((int) point.getX() + preX,
			(int) point.getY() + preY);
		selectedNode.setLocation(newPosition);
		repaint();
	    }
	}
    }

    @Override
    public void update(Observable observable, Object update) {
	if (observable instanceof Topology) {
	    TopologyUpdateEvent event = (TopologyUpdateEvent) update;

	    if (event.getAction() == TopologyUpdate.ROUTER_ADDED) {
		addRouter(event.getRouter());
	    } else if (event.getAction() == TopologyUpdate.EDGE_ADDED) {
		Router[] routers = event.getRouterPair();
		addEdge(routers[0], routers[1]);
	    } else if (event.getAction() == TopologyUpdate.ROUTER_REMOVED) {
		removeRouter(event.getRouter());
	    } else if (event.getAction() == TopologyUpdate.EDGE_REMOVED) {
		Router[] routers = event.getRouterPair();
		removeEdge(routers[0], routers[1]);
	    }
	}
    }
}
