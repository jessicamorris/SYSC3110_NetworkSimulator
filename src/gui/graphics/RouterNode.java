package gui.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import network.Packet;
import network.Router;

/**
 * A Component that represents a Router in the canvas of a SimulatorPanel
 * 
 * @author Jessica
 * 
 */
@SuppressWarnings("serial")
public class RouterNode extends JComponent implements Comparable<RouterNode> {

    private static final int HEIGHT = 35;
    private static final int BORDERSIZE = 1;
    private static final Color STANDARD_FILL = new Color(80, 130, 185);
    private static final Color STANDARD_BORDER = new Color(90, 145, 210);
    private static final Color HIGHLIGHT_FILL = new Color(100, 190, 70);
    private static final Color HIGHLIGHT_BORDER = new Color(100, 205, 80);

    private final Ellipse2D displayShape;
    private final Router router;
    private final int width;
    private int x;
    private int y;
    private final List<RouterEdge> edges;

    /**
     * Creates a RouterNode which references a specific Router to be drawn at a
     * certain x, y position on the canvas
     * 
     * @param router
     *            The Router that this RouterNode will represent
     * @param x
     *            The x position
     * @param y
     *            The y position
     */
    public RouterNode(Router router, int x, int y) {
	this.router = router;
	width = HEIGHT + 5 * (router.getName().length());
	displayShape = new Ellipse2D.Double(x, y, width, HEIGHT);
	this.x = x;
	this.y = y;
	edges = new ArrayList<>();
    }

    /**
     * Connects this RouterNode to another RouterNode using a RouterEdge
     * 
     * @param dest
     *            the RouterNode to connect to this RouterNode
     */
    public void addEdge(RouterNode dest) {
	RouterEdge edge = new RouterEdge(this, dest);
	edges.add(edge);
	dest.edges.add(edge);
    }

    /**
     * Disconnects this RouterNode from another RouterNode using a RouterEdge
     * 
     * @param dest
     *            the RouterNode to disconnect from this RouterNode
     * @return whether the operation was successful
     */
    public boolean removeEdge(RouterNode dest) {
	for (RouterEdge edge : edges) {
	    if (edge.containsNode(dest)) {
		edges.remove(edge);
		dest.edges.remove(edge);
		return true;
	    }
	}
	return false;
    }

    /**
     * @return The list of edges this RouterNode has
     */
    protected List<RouterEdge> getEdges() {
	return edges;
    }

    @Override
    public void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

	Color border = null, fill = null;

	// Pick colour
	for (Packet packet : router.getPackets()) {
	    if (packet.getSource() == router && packet.getHops() == 0) {
		border = HIGHLIGHT_BORDER;
		fill = HIGHLIGHT_FILL;
		break;
	    }
	}
	if (border == null) {
	    border = STANDARD_BORDER;
	    fill = STANDARD_FILL;
	}

	// Draw outer ellipse - this is the border
	Ellipse2D displayBorder = new Ellipse2D.Double(x - BORDERSIZE, y
		- BORDERSIZE, width + 2 * BORDERSIZE, HEIGHT + 2 * BORDERSIZE);
	g2d.setColor(border);
	g2d.fill(displayBorder);

	// Draw inner ellipse
	g2d.setColor(fill);
	g2d.fill(displayShape);

	// Draw router name label on top
	String routerName = router.getName();
	g2d.setColor(Color.WHITE);
	g2d.drawString(routerName, (int) displayShape.getCenterX() - 3
		* routerName.length(), (int) displayShape.getCenterY() + 5);
    }

    /**
     * @return the Router that this RouterNode represents
     * @see Router
     */
    public Router getRouter() {
	return router;
    }

    @Override
    public boolean contains(int x, int y) {
	return displayShape.contains(x, y, width, HEIGHT);
    }

    @Override
    public boolean contains(Point point) {
	return displayShape.contains(point);
    }

    @Override
    public void setLocation(Point p) {
	this.x = (int) p.getX();
	this.y = (int) p.getY();
	displayShape.setFrame(x, y, width, HEIGHT);
    }

    /**
     * @return The x location of the centre of the shape that is drawn on the
     *         canvas
     */
    public double getCenterX() {
	return displayShape.getCenterX();
    }

    /**
     * @return The y location of the centre of the shape that is drawn on the
     *         canvas
     */
    public double getCenterY() {
	return displayShape.getCenterY();
    }

    @Override
    public int getX() {
	return x;
    }

    @Override
    public int getY() {
	return y;
    }

    @Override
    public int getWidth() {
	return width;
    }

    @Override
    public int getHeight() {
	return HEIGHT;
    }

    @Override
    public int compareTo(RouterNode node) {
	return this.router.compareTo(node.router);
    }
}