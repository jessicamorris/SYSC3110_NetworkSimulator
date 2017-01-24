package gui;

import gui.controller.TopologyButtonListener;
import gui.graphics.TopologyCanvas;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import network.Topology;
import simulation.Simulation;

import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;
import common.TopologyUpdateEvent;
import common.TopologyUpdateEvent.TopologyUpdate;

@SuppressWarnings("serial")
public class TopologyPanel extends JPanel implements Observer {

    private final JButton addRouter;
    private final JButton removeRouter;
    private final JButton addEdge;
    private final JButton removeEdge;
    private final TopologyCanvas canvas;

    public TopologyPanel() {
	super(new GridBagLayout());
	ActionListener listener = new TopologyButtonListener();

	addRouter = new JButton("Add Router");
	addRouter.setActionCommand(TopologyUpdate.ROUTER_ADDED.name());
	addRouter.addActionListener(listener);

	removeRouter = new JButton("Remove Router");
	removeRouter.setActionCommand(TopologyUpdate.ROUTER_REMOVED.name());
	removeRouter.setEnabled(false);
	removeRouter.addActionListener(listener);

	addEdge = new JButton("Add Edge");
	addEdge.setActionCommand(TopologyUpdate.EDGE_ADDED.name());
	addEdge.setEnabled(false);
	addEdge.addActionListener(listener);

	removeEdge = new JButton("Remove Edge");
	removeEdge.setActionCommand(TopologyUpdate.EDGE_REMOVED.name());
	removeEdge.setEnabled(false);
	removeEdge.addActionListener(listener);

	canvas = new TopologyCanvas();

	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 1.0;
	add(addRouter, c);

	c.gridx = 1;
	add(removeRouter, c);

	c.gridx = 2;
	add(addEdge, c);

	c.gridx = 3;
	add(removeEdge, c);

	c.fill = GridBagConstraints.BOTH;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.gridheight = GridBagConstraints.REMAINDER;
	c.gridx = 0;
	c.gridy = 1;
	c.weighty = 1.0;
	add(new JScrollPane(canvas), c);

	Simulation.getInstance().addObserver(this);
	Simulation.getInstance().getTopology().addObserver(canvas);
    }

    @Override
    public void update(Observable observable, Object update) {
	if (observable instanceof Topology) {
	    TopologyUpdateEvent event = (TopologyUpdateEvent) update;
	    if (event.getAction() == TopologyUpdate.ROUTER_ADDED) {
		removeRouter.setEnabled(true);
		if (event.getRouterCount() >= 2) {
		    addEdge.setEnabled(true);
		}
	    } else if (event.getAction() == TopologyUpdate.ROUTER_REMOVED) {
		if (event.getRouterCount() < 2) {
		    addEdge.setEnabled(false);
		    removeEdge.setEnabled(false);
		}

		if (event.getRouterCount() < 1) {
		    removeRouter.setEnabled(false);
		}

	    } else if (event.getAction() == TopologyUpdate.EDGE_ADDED) {
		removeEdge.setEnabled(true);
	    } else if (event.getAction() == TopologyUpdate.EDGE_REMOVED) {
		if (event.getEdgeCount() < 1) {
		    removeEdge.setEnabled(false);
		}
	    }
	} else if (observable instanceof Simulation) {
	    SimulationUpdateEvent event = (SimulationUpdateEvent) update;
	    if (event.getAction() == SimulationUpdate.TOPOLOGY_RESET) {
		canvas.reset();
		Simulation.getInstance().getTopology().addObserver(canvas);

		removeRouter.setEnabled(false);
		addEdge.setEnabled(false);
		removeEdge.setEnabled(false);
	    } else if (event.getAction() == SimulationUpdate.STEP_TAKEN
		    || event.getAction() == SimulationUpdate.UNDO_STEP) {
		canvas.repaint();
	    }
	}
    }
}
