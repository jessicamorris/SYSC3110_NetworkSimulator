package gui;

import gui.models.AverageHopsTableModel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigInteger;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import simulation.Simulation;
import simulation.StepResult;

import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;

@SuppressWarnings("serial")
public class MetricsPanel extends JPanel implements Observer {

    private BigInteger packetCount;

    private final JTextField stepCount;
    private final JTextField totalPackets;

    public MetricsPanel() {
	super(new GridBagLayout());

	packetCount = BigInteger.ZERO;

	stepCount = new JTextField();
	stepCount.setEditable(false);
	totalPackets = new JTextField();
	totalPackets.setEditable(false);

	GridBagConstraints c = new GridBagConstraints();

	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridwidth = 1;
	c.gridx = 0;
	c.gridy = 0;
	c.insets = new Insets(5, 5, 5, 5);
	c.weightx = 0.0;
	add(new JLabel("Total steps:"), c);

	c.gridwidth = GridBagConstraints.REMAINDER;
	c.gridx = 1;
	c.insets = new Insets(0, 0, 0, 0);
	c.weightx = 1.0;
	add(stepCount, c);

	c.gridwidth = 1;
	c.gridx = 0;
	c.gridy = 1;
	c.insets = new Insets(5, 5, 5, 5);
	c.weightx = 0.0;
	add(new JLabel("Total packets:"), c);

	c.gridwidth = GridBagConstraints.REMAINDER;
	c.gridx = 1;
	c.insets = new Insets(0, 0, 0, 0);
	c.weightx = 1.0;
	add(totalPackets, c);

	c.fill = GridBagConstraints.BOTH;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.gridx = 0;
	c.gridy = 2;
	c.insets = new Insets(0, 0, 0, 0);
	c.weighty = 1.0;
	c.weightx = 1.0;
	JTable table = new CenteredJTable(new AverageHopsTableModel());
	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	renderer.setHorizontalAlignment(SwingConstants.CENTER);
	table.setDefaultRenderer(String.class, renderer);
	add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), c);

	Simulation.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object update) {
	if (observable instanceof Simulation) {
	    SimulationUpdateEvent event = (SimulationUpdateEvent) update;

	    if (event.getAction() == SimulationUpdate.STEP_TAKEN) {
		StepResult result = event.getStepResult();
		stepCount.setText(result.getStepNumber().toString());

		packetCount = packetCount.add(result.getPacketsTransmitted());
		totalPackets.setText(packetCount.toString());
	    } else if (event.getAction() == SimulationUpdate.METRICS_RESET) {
		stepCount.setText("");
		totalPackets.setText("");
		packetCount = BigInteger.ZERO;
	    }
	}
    }
}
