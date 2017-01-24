package gui.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class RouterEdge extends JComponent {

    private final RouterNode start;
    private final RouterNode end;
    private final Line2D displayShape;
    private static final Color STANDARD = new Color(0x5B, 0x9B, 0xD5);

    public RouterEdge(RouterNode start, RouterNode end) {
	this.start = start;
	this.end = end;
	displayShape = new Line2D.Double(start.getCenterX(),
		start.getCenterY(), end.getCenterX(), end.getCenterY());
    }

    public boolean containsNode(RouterNode node) {
	return (start == node) || (end == node);
    }

    @Override
    public void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;

	g2d.setColor(STANDARD);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

	// Update displayShape's location based on its start and end nodes
	displayShape.setLine(start.getCenterX(), start.getCenterY(),
		end.getCenterX(), end.getCenterY());
	g2d.draw(displayShape);

    }
}
