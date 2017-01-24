package gui.models;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;

import simulation.Simulation;

import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;

@SuppressWarnings("serial")
public class LogListModel<StepResult> extends AbstractListModel<StepResult>
	implements Observer {

    private final int maximumElements;
    private final List<StepResult> log;
    private final Deque<StepResult> oldLogs;

    public LogListModel(int maximumElements) throws IOException {
	this.maximumElements = maximumElements;
	log = new ArrayList<>();
	oldLogs = new ArrayDeque<>();

	Simulation.getInstance().addObserver(this);
    }

    @Override
    public StepResult getElementAt(int i) {
	return log.get(i);
    }

    @Override
    public int getSize() {
	return log.size();
    }

    private void add(int index, StepResult element) {
	log.add(index, element);
	fireIntervalAdded(this, index, index);

	if (log.size() > maximumElements) {
	    // TODO: Write to log file
	    oldLogs.push(log.remove(maximumElements));

	    fireIntervalRemoved(this, maximumElements, maximumElements);
	}
    }

    private void addElement(StepResult element) {
	add(0, element);
    }

    private void removeLatest() {
	log.remove(0);
	fireIntervalRemoved(this, 0, 0);
    }

    private void clear() {
	int previousSize = log.size();
	log.clear();
	oldLogs.clear();
	fireIntervalRemoved(this, 0, previousSize - 1);
    }

    @Override
    public void update(Observable observable, Object update) {
	if (observable instanceof Simulation) {
	    SimulationUpdateEvent event = (SimulationUpdateEvent) update;

	    if (event.getAction() == SimulationUpdate.STEP_TAKEN) {
		@SuppressWarnings("unchecked")
		StepResult result = (StepResult) event.getStepResult();
		addElement(result);
	    } else if (event.getAction() == SimulationUpdate.METRICS_RESET) {
		clear();
	    } else if (event.getAction() == SimulationUpdate.UNDO_STEP) {
		removeLatest();
	    }
	}
    }

}
