package gui.controller;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import simulation.Simulation;
import algorithm.RoutingAlgorithm;

import common.Action;

public class MainMenuListener implements ActionListener {

    public MainMenuListener() {
	super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String actionCommand = e.getActionCommand();

	if (actionCommand == Action.MainMenu.RESET_SIMULATION.name()) {
	    if (JOptionPane.YES_OPTION == JOptionPane
		    .showConfirmDialog(
			    null,
			    "If you do this, you will erase the entire simulation setup!\nContinue?",
			    "Confirm Simulation Delete",
			    JOptionPane.YES_NO_OPTION)) {
		Simulation.getInstance().resetMetrics();

	    }
	} else if (actionCommand == Action.MainMenu.CONFIGURATION_UPDATE.name()) {
	    ConfigurationPanel dialogPanel = new ConfigurationPanel();
	    boolean quit = false;

	    do {
		if (JOptionPane.OK_OPTION == JOptionPane
			.showConfirmDialog(null, dialogPanel,
				"Simulation Configuration",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE)) {
		    if (!(dialogPanel.getRate() > 0)) {
			JOptionPane
				.showMessageDialog(
					null,
					"For the rate, you must enter a valid positive integer greater than 0.",
					"Error", JOptionPane.ERROR_MESSAGE);
			dialogPanel.reset();
		    } else {
			boolean change = true;
			if (Simulation.getInstance().hasStarted()) {
			    change = (JOptionPane.YES_OPTION == JOptionPane
				    .showConfirmDialog(
					    null,
					    "Changing the simulation parameters now will reset the simulation! Do you wish to continue?",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE,
					    JOptionPane.YES_NO_OPTION));
			}

			if (change) {
			    Simulation.getInstance().setRate(
				    dialogPanel.getRate());
			    Simulation.getInstance().setRoutingAlgorithm(
				    dialogPanel.getRoutingAlgorithm());
			    quit = true;
			} else {
			    dialogPanel.reset();
			}
		    }
		} else {
		    quit = true;
		}
	    } while (!quit);

	} else if (actionCommand == Action.MainMenu.RESET_TOPOLOGY.name()) {
	    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
		    "Are you sure you want to erase the current topology?",
		    "Confirm Topology Reset", JOptionPane.YES_NO_OPTION)) {
		Simulation.getInstance().newTopology();
	    }
	} else if (actionCommand == Action.MainMenu.LOAD_TOPOLOGY.name()) {
	    if (JOptionPane.YES_OPTION == JOptionPane
		    .showConfirmDialog(
			    null,
			    "Loading a new topology will erase your current one.\nDo you wish to continue?",
			    "Confirm Load Topology", JOptionPane.YES_NO_OPTION)) {
		// TODO: Implement this
	    }
	} else if (actionCommand == Action.MainMenu.SAVE_TOPOLOGY.name()) {
	    // TODO: Implement this
	}
    }

    @SuppressWarnings("serial")
    private class ConfigurationPanel extends JPanel {
	private final JTextField rate;
	private final JComboBox<RoutingAlgorithm> algorithms;

	public ConfigurationPanel() {
	    super(new GridBagLayout());

	    rate = new JTextField(Simulation.getInstance().getRate() + "");
	    algorithms = new JComboBox<>();

	    for (RoutingAlgorithm algorithm : RoutingAlgorithm.getSubclasses()) {
		algorithms.addItem(algorithm);
	    }

	    RoutingAlgorithm current = (Simulation.getInstance()
		    .getRoutingAlgorithm());
	    algorithms.setSelectedIndex((current == null) ? -1
		    : RoutingAlgorithm.indexof(current));

	    GridBagConstraints c = new GridBagConstraints();

	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.gridy = 0;
	    c.insets = new Insets(5, 5, 10, 5);
	    JLabel instructions = new JLabel(
		    "To modify the simulation set-up, change the details here and press OK.");
	    instructions.setHorizontalAlignment(SwingConstants.CENTER);
	    Font f = instructions.getFont();
	    instructions.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
	    add(instructions, c);

	    c.fill = GridBagConstraints.NONE;
	    c.gridx = 0;
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.gridy = 1;
	    c.weightx = 0.0;
	    c.insets = new Insets(5, 5, 5, 5);
	    add(new JLabel("Rate:"), c);

	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 1;
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.weightx = 1.0;
	    add(rate, c);

	    c.fill = GridBagConstraints.NONE;
	    c.gridx = 0;
	    c.gridwidth = GridBagConstraints.RELATIVE;
	    c.gridy = 2;
	    c.weightx = 0.0;
	    c.insets = new Insets(5, 5, 5, 5);
	    add(new JLabel("Routing Algorithm:"), c);

	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 1;
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.weightx = 1.0;
	    add(algorithms, c);
	}

	public void reset() {
	    rate.setText(Simulation.getInstance().getRate() + "");
	}

	public long getRate() {
	    try {
		return Long.parseLong(rate.getText());
	    } catch (NumberFormatException e) {
		return -1;
	    }
	}

	public RoutingAlgorithm getRoutingAlgorithm() {
	    return (RoutingAlgorithm) algorithms.getSelectedItem();
	}
    }

}
