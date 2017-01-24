package gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import network.Router;
import simulation.Simulation;

import common.TopologyUpdateEvent.TopologyUpdate;

public class TopologyButtonListener implements ActionListener {

    public TopologyButtonListener() {
	super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String actionCommand = e.getActionCommand();

	if (Simulation.getInstance().hasStarted()) {
	    if (JOptionPane.NO_OPTION == JOptionPane
		    .showConfirmDialog(
			    null,
			    "Changing the topology now will reset the simulation.\nContinue?",
			    "Warning", JOptionPane.YES_NO_OPTION,
			    JOptionPane.WARNING_MESSAGE)) {
		return;
	    }
	}

	// Add Router
	if (actionCommand == TopologyUpdate.ROUTER_ADDED.name()) {
	    String name = JOptionPane.showInputDialog(null,
		    "Enter the name of the new router:", "Add Router",
		    JOptionPane.QUESTION_MESSAGE);

	    if (name != null) {
		try {
		    Router router = new Router(name);

		    if (!Simulation.getInstance().getTopology()
			    .addRouter(router)) {
			JOptionPane.showMessageDialog(null,
				"A Router with that name already exists!",
				"Error", JOptionPane.ERROR_MESSAGE);
		    }
		} catch (IllegalArgumentException ex) {
		    JOptionPane.showMessageDialog(null,
			    "You can't add a Router with an empty name.",
			    "Error", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}

	// Add Edge
	else if (actionCommand == TopologyUpdate.EDGE_ADDED.name()) {
	    // Create a copy of the list of all routers in the network
	    Set<Router> routers = new TreeSet<>();
	    for (Router router : Simulation.getInstance().getTopology()) {
		routers.add(router);
	    }
	    Object[] values = routers.toArray();

	    Router router1 = (Router) JOptionPane.showInputDialog(null,
		    "Select the first router to connect:", "Add Edge",
		    JOptionPane.QUESTION_MESSAGE, null, values, values[0]);

	    if (router1 != null) {
		routers.remove(router1);
		routers.removeAll(router1.getConnections());

		if (routers.size() == 0) {
		    JOptionPane.showMessageDialog(null,
			    "That Router is already connected to everything!",
			    "Error", JOptionPane.ERROR_MESSAGE);
		} else {
		    values = routers.toArray();
		    Router router2 = (Router) JOptionPane.showInputDialog(null,
			    "Select the second router to connect:", "Add Edge",
			    JOptionPane.QUESTION_MESSAGE, null, values,
			    values[0]);
		    if (router2 != null) {
			Simulation.getInstance().getTopology()
				.addEdge(router1, router2);
		    }
		}
	    }
	}

	// Remove Router
	else if (actionCommand == TopologyUpdate.ROUTER_REMOVED.name()) {
	    Object[] values = Simulation.getInstance().getTopology().toArray();
	    Router router = (Router) JOptionPane.showInputDialog(null,
		    "Select a router to remove:", "Remove Router",
		    JOptionPane.QUESTION_MESSAGE, null, values, values[0]);

	    if (router != null) {
		Simulation.getInstance().getTopology().removeRouter(router);
	    }
	}

	// Remove Edge
	else if (actionCommand == TopologyUpdate.EDGE_REMOVED.name()) {
	    Object[] values = Simulation.getInstance().getTopology().toArray();

	    Router router1 = (Router) JOptionPane.showInputDialog(null,
		    "Select the first router to disconnect:", "Remove Edge",
		    JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
	    if (router1 != null) {
		values = router1.getConnections().toArray();
		Router router2 = (Router) JOptionPane.showInputDialog(null,
			"Select the second router to disconnect:",
			"Remove Edge", JOptionPane.QUESTION_MESSAGE, null,
			values, values[0]);

		if (router2 != null) {
		    Simulation.getInstance().getTopology()
			    .removeEdge(router1, router2);
		}
	    }
	}
    }
}
