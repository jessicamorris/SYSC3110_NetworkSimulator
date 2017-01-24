package gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import simulation.Simulation;

import common.Action;

public class StepButtonListener implements ActionListener {

    public StepButtonListener() {
	super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	String action = e.getActionCommand();

	if (action == Action.Step.STEP_ONCE.name()) {
	    doSteps(1);
	} else if (action == Action.Step.STEP_N_TIMES.name()) {
	    String result = null;
	    int value = -1;

	    do {
		result = JOptionPane
			.showInputDialog("Enter the number of steps:");

		try {
		    value = Integer.parseUnsignedInt(result);
		    doSteps(value);
		} catch (NumberFormatException ex) {
		    JOptionPane
			    .showMessageDialog(
				    null,
				    "You must enter a valid positive integer greater than 0.",
				    "Error", JOptionPane.ERROR_MESSAGE);
		}
	    } while (result != null && value < 0);

	} else if (action == Action.Step.UNDO_STEP.name()) {
	    Simulation.getInstance().undoStep();
	}
    }

    private void doSteps(int steps) {
	for (int i = 0; i < steps; i++) {
	    Simulation.getInstance().step();
	}
    }
}
