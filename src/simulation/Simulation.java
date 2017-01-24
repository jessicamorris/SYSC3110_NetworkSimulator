package simulation;

import java.math.BigInteger;
import java.util.Observable;
import java.util.Observer;

import network.Topology;
import algorithm.RoutingAlgorithm;

import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;
import common.TopologyUpdateEvent;
import common.TopologyUpdateEvent.TopologyUpdate;

public class Simulation extends Observable implements Observer {

    private static Simulation instance = new Simulation(1);
    private long rate;
    private boolean isReady;
    private Topology topology;
    private RoutingAlgorithm algorithm;

    private BigInteger stepNumber;
    private BigInteger packetCount;

    private Simulation(long rate) {
	this.rate = rate;
	stepNumber = BigInteger.ZERO;
	packetCount = BigInteger.ZERO;
	isReady = false;

	topology = new Topology();
	topology.addObserver(this);
	algorithm = null;
    }

    public static Simulation getInstance() {
	return instance;
    }

    public void setTopology(Topology topology) {
	this.topology = topology;

	setChanged();
	notifyObservers(new SimulationUpdateEvent(
		SimulationUpdate.TOPOLOGY_RESET, null));
    }

    public Topology getTopology() {
	return topology;
    }

    public void setRoutingAlgorithm(RoutingAlgorithm algorithm) {
	if (this.algorithm == null) {
	    this.algorithm = algorithm;

	    if (readyStateChanged()) {
		setChanged();
		notifyObservers(new SimulationUpdateEvent(
			SimulationUpdate.READY_STATE_CHANGED, isReady));
	    }
	} else {
	    this.algorithm = algorithm;

	    setChanged();
	    notifyObservers(new SimulationUpdateEvent(
		    SimulationUpdate.ALGORITHM_CHANGED, algorithm));
	}

    }

    public RoutingAlgorithm getRoutingAlgorithm() {
	return algorithm;
    }

    public void newTopology() {
	this.topology = new Topology();

	if (stepNumber.compareTo(BigInteger.ONE) > 0) {
	    resetMetrics();
	}

	setChanged();
	notifyObservers(new SimulationUpdateEvent(
		SimulationUpdate.TOPOLOGY_RESET, null));
    }

    public long getRate() {
	return rate;
    }

    public void setRate(long rate) {
	this.rate = rate;

	setChanged();
	notifyObservers(new SimulationUpdateEvent(
		SimulationUpdate.RATE_CHANGED, rate));

	if (stepNumber.compareTo(BigInteger.ONE) > 0) {
	    resetMetrics();
	}
    }

    public boolean hasStarted() {
	return stepNumber.compareTo(BigInteger.ZERO) != 0;
    }

    public boolean readyStateChanged() {
	boolean newState = (algorithm != null) && topology.isValid();
	if (newState != isReady) {
	    isReady = newState;

	    return true;
	} else {
	    return false;
	}
    }

    public BigInteger getStepNumber() {
	return stepNumber;
    }

    public void resetMetrics() {
	topology.resetPackets();
	stepNumber = BigInteger.ZERO;
	packetCount = BigInteger.ZERO;

	setChanged();
	notifyObservers(new SimulationUpdateEvent(
		SimulationUpdate.METRICS_RESET, null));
    }

    public void step() {
	if (stepNumber.equals(BigInteger.ZERO)) {
	    algorithm.buildRoutingTables(topology);
	}

	boolean doInject = stepNumber.mod(new BigInteger(rate + "")) == BigInteger.ZERO;
	stepNumber = stepNumber.add(BigInteger.ONE);

	StepResult result = algorithm.step(topology);

	if (doInject) {
	    result.addPacketInjectedMessage(topology.injectPacket(packetCount
		    .toString()));
	    packetCount = packetCount.add(BigInteger.ONE);
	}

	if (stepNumber.equals(BigInteger.ONE)) {
	    // First step taken, trigger an update
	    setChanged();
	    notifyObservers(new SimulationUpdateEvent(
		    SimulationUpdate.FIRST_STEP_TAKEN, null));
	}

	setChanged();
	notifyObservers(new SimulationUpdateEvent(SimulationUpdate.STEP_TAKEN,
		result));
    }

    public void undoStep() {
	if (stepNumber.compareTo(BigInteger.ZERO) > 0) {
	    algorithm.undoStep(topology);

	    setChanged();
	    notifyObservers(new SimulationUpdateEvent(
		    SimulationUpdate.UNDO_STEP, null));
	    stepNumber = stepNumber.subtract(BigInteger.ONE);

	    if (stepNumber.mod(new BigInteger(rate + "")) == BigInteger.ZERO) {
		packetCount = packetCount.subtract(BigInteger.ONE);
	    }
	}
    }

    @Override
    public void addObserver(Observer obs) {
	super.addObserver(obs);
	topology.addObserver(obs);
    }

    @Override
    public void update(Observable observable, Object update) {
	if (observable instanceof Topology) {
	    TopologyUpdateEvent event = (TopologyUpdateEvent) update;
	    if (event.getAction() == TopologyUpdate.ROUTER_ADDED
		    || event.getAction() == TopologyUpdate.ROUTER_REMOVED
		    || event.getAction() == TopologyUpdate.EDGE_ADDED
		    || event.getAction() == TopologyUpdate.EDGE_REMOVED) {
		if (stepNumber.compareTo(BigInteger.ONE) > 0) {
		    resetMetrics();
		}

		if (readyStateChanged()) {
		    setChanged();
		    notifyObservers(new SimulationUpdateEvent(
			    SimulationUpdate.READY_STATE_CHANGED, isReady));
		}
	    }
	}
    }
}
