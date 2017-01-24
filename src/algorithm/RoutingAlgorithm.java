package algorithm;

import java.util.ArrayList;
import java.util.List;

import network.Router;
import network.Topology;
import simulation.StepResult;

public abstract class RoutingAlgorithm {

    private enum RoutingAlgorithmSubclass {

	FLOODING(FloodingAlgorithm.class), RANDOM_PATH(
		RandomPathAlgorithm.class), SHORTEST_PATH(
		ShortestPathAlgorithm.class);

	private RoutingAlgorithm algorithm;

	private RoutingAlgorithmSubclass(Class<?> type) {
	    try {
		algorithm = (RoutingAlgorithm) type.newInstance();
	    } catch (InstantiationException | IllegalAccessException ex) {
		algorithm = null;
	    }
	}

	public RoutingAlgorithm getInstance() {
	    return algorithm;
	}

	public static int indexof(RoutingAlgorithm algorithm) {
	    for (int i = 0; i < values().length; i++) {
		if (algorithm == values()[i].getInstance()) {
		    return i;
		}
	    }
	    return -1;
	}
    }

    public static RoutingAlgorithm[] getSubclasses() {
	List<RoutingAlgorithm> subclasses = new ArrayList<>();

	for (RoutingAlgorithmSubclass a : RoutingAlgorithmSubclass.values()) {
	    subclasses.add(a.getInstance());
	}

	RoutingAlgorithm[] subclassesArray = new RoutingAlgorithm[subclasses
		.size()];
	subclassesArray = subclasses.toArray(subclassesArray);
	return subclassesArray;
    }

    public static int indexof(RoutingAlgorithm algorithm) {
	return RoutingAlgorithmSubclass.indexof(algorithm);
    }

    public abstract void buildRoutingTables(Topology topology);

    public abstract StepResult step(Topology topology);

    public void undoStep(Topology topology) {
	for (Router router : topology) {
	    router.unsendPackets();
	}

	for (Router router : topology) {
	    router.dropRepeatedPackets();
	}
    }

    @Override
    public abstract String toString();
}
