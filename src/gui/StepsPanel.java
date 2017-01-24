package gui;

import gui.controller.StepButtonListener;
import gui.models.LogListModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import simulation.Simulation;
import simulation.StepResult;

import common.Action;
import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;

@SuppressWarnings("serial")
public class StepsPanel extends JPanel implements Observer {

    private final JButton stepOnce;
    private final JButton stepNTimes;
    private final JButton undoStep;
    private final JList<StepResult> stepLog;

    // Magic constants
    private final int width = 200;
    private final int maximumElements = 50;
    private final int buttonHeight = 25;

    public StepsPanel() {
	super(new GridBagLayout());
	ActionListener listener = new StepButtonListener();

	stepOnce = new JButton("Step Once");
	stepOnce.setEnabled(false);
	stepOnce.setActionCommand(Action.Step.STEP_ONCE.name());
	stepOnce.addActionListener(listener);
	stepNTimes = new JButton("Step N Times");
	stepNTimes.setEnabled(false);
	stepNTimes.setActionCommand(Action.Step.STEP_N_TIMES.name());
	stepNTimes.addActionListener(listener);
	undoStep = new JButton("Undo Step");
	undoStep.setEnabled(false);
	undoStep.setActionCommand(Action.Step.UNDO_STEP.name());
	undoStep.addActionListener(listener);

	stepLog = new JList<>();
	try {
	    stepLog.setModel(new LogListModel<StepResult>(maximumElements));
	} catch (IOException ex) {
	    JOptionPane
		    .showMessageDialog(
			    null,
			    "There was an error creating a temporary file for saving log elements.\nUndoing steps will not update the step log properly.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	stepLog.setAutoscrolls(true);
	stepLog.setSelectionModel(new DefaultListSelectionModel() {
	    @Override
	    public void setSelectionInterval(int index0, int index1) {
		super.setSelectionInterval(-1, -1);
	    }
	});
	stepLog.setVisibleRowCount(JList.HORIZONTAL_WRAP);
	stepLog.setCellRenderer(new TextWrappingRenderer(width));

	JPanel logContainer = new JPanel(new BorderLayout());
	logContainer.setBorder(BorderFactory.createTitledBorder(
		BorderFactory.createEtchedBorder(), "Step Log",
		TitledBorder.CENTER, TitledBorder.TOP));
	logContainer.add(new JScrollPane(stepLog,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

	GridBagConstraints c = new GridBagConstraints();

	c.gridx = 0;
	c.gridy = 0;
	c.ipady = buttonHeight;
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridwidth = GridBagConstraints.REMAINDER;
	add(stepOnce, c);

	c.ipadx = 0;
	c.gridy = 1;
	add(stepNTimes, c);

	c.gridy = 2;
	add(undoStep, c);

	c.fill = GridBagConstraints.BOTH;
	c.gridy = 3;
	c.insets = new Insets(10, 0, 0, 0);
	c.ipady = 0;
	c.weighty = 1.0;
	add(logContainer, c);

	Simulation.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object update) {
	if (observable instanceof Simulation) {
	    SimulationUpdateEvent event = (SimulationUpdateEvent) update;

	    if (event.getAction() == SimulationUpdate.READY_STATE_CHANGED) {
		stepOnce.setEnabled(event.getNewReadyState());
		stepNTimes.setEnabled(event.getNewReadyState());
	    } else if (event.getAction() == SimulationUpdate.FIRST_STEP_TAKEN) {
		undoStep.setEnabled(true);
	    } else if (event.getAction() == SimulationUpdate.METRICS_RESET
		    || event.getAction() == SimulationUpdate.TOPOLOGY_RESET) {
		stepOnce.setEnabled(true);
		stepNTimes.setEnabled(true);
		undoStep.setEnabled(false);
	    }
	}
    }

    private class TextWrappingRenderer extends DefaultListCellRenderer {
	private static final String HTML_1 = "<html><body style='font-weight: normal; width: ";
	private static final String HTML_2 = "px'>";
	private static final String HTML_3 = "</body></html>";
	private final int width;

	/**
	 * @param width
	 *            The width, in pixels, of the logView. (This should be the
	 *            same as the width of the buttons!)
	 */
	public TextWrappingRenderer(int width) {
	    this.width = width;
	}

	/**
	 * Render the cell.
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> list,
		Object value, int index, boolean isSelected,
		boolean cellHasFocus) {
	    String text = HTML_1 + String.valueOf(width) + HTML_2
		    + value.toString() + HTML_3;
	    return super.getListCellRendererComponent(list, text, index,
		    isSelected, cellHasFocus);
	}

    }

}
