package gui;

import gui.controller.MainMenuListener;

import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import simulation.Simulation;

import common.Action;
import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;

@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar implements Observer {

    private final JMenuItem configureSimulation;
    private final JMenuItem resetSimulator;
    private final JMenuItem newTopology;
    private final JMenuItem loadTopology;
    private final JMenuItem saveTopology;

    /**
     * Creates a new MainMenuBar
     */
    public MainMenuBar() {
	JMenu simulatorMenu = new JMenu("Simulation");
	add(simulatorMenu);
	JMenu topologyMenu = new JMenu("Topology");
	add(topologyMenu);
	ActionListener listener = new MainMenuListener();

	configureSimulation = new JMenuItem("Configure");
	configureSimulation
		.setActionCommand(Action.MainMenu.CONFIGURATION_UPDATE.name());
	simulatorMenu.add(configureSimulation);
	configureSimulation.addActionListener(listener);

	resetSimulator = new JMenuItem("Reset");
	resetSimulator
		.setActionCommand(Action.MainMenu.RESET_SIMULATION.name());
	resetSimulator.setEnabled(false);
	simulatorMenu.add(resetSimulator);
	resetSimulator.addActionListener(listener);

	newTopology = new JMenuItem("New");
	newTopology.setActionCommand(Action.MainMenu.RESET_TOPOLOGY.name());
	topologyMenu.add(newTopology);
	newTopology.addActionListener(listener);

	loadTopology = new JMenuItem("Load...");
	loadTopology.setActionCommand(Action.MainMenu.LOAD_TOPOLOGY.name());
	loadTopology.setEnabled(false);
	topologyMenu.add(loadTopology);
	loadTopology.addActionListener(listener);

	saveTopology = new JMenuItem("Save As...");
	saveTopology.setActionCommand(Action.MainMenu.SAVE_TOPOLOGY.name());
	saveTopology.setEnabled(false);
	topologyMenu.add(saveTopology);
	saveTopology.addActionListener(listener);

	Simulation.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object update) {
	// Simulation status update causes re-evaluation of elements enabled
	if (observable instanceof Simulation) {
	    SimulationUpdateEvent event = (SimulationUpdateEvent) update;
	    if (event.getAction() == SimulationUpdate.FIRST_STEP_TAKEN) {
		resetSimulator.setEnabled(true);
	    } else if (event.getAction() == SimulationUpdate.READY_STATE_CHANGED) {
		boolean newState = event.getNewReadyState();
		loadTopology.setEnabled(newState);
		saveTopology.setEnabled(newState);
	    }
	}
    }
}
