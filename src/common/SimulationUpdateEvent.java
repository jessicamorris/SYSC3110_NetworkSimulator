package common;

import simulation.StepResult;

public class SimulationUpdateEvent {

    public static enum SimulationUpdate {
	RATE_CHANGED, ALGORITHM_CHANGED, READY_STATE_CHANGED, FIRST_STEP_TAKEN, STEP_TAKEN, METRICS_RESET, TOPOLOGY_RESET, UNDO_STEP
    }

    private final SimulationUpdate action;
    private final Object update;

    public SimulationUpdateEvent(SimulationUpdate action, Object update) {
	this.action = action;
	this.update = update;
    }

    public SimulationUpdate getAction() {
	return action;
    }

    public int getNewRate() {
	return (int) update;
    }

    public boolean getNewReadyState() {
	return (boolean) update;
    }

    public StepResult getStepResult() {
	return (StepResult) update;
    }
}
