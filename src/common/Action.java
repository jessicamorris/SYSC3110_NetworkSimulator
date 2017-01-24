package common;

public class Action {

    public static enum MainMenu {
	CONFIGURATION_UPDATE, RESET_SIMULATION, SAVE_TOPOLOGY, LOAD_TOPOLOGY, RESET_TOPOLOGY
    }

    public static enum Step {
	STEP_ONCE, STEP_N_TIMES, UNDO_STEP
    }
}
