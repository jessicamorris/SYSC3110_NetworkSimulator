package algorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import network.Packet;
import network.Router;
import network.Topology;
import simulation.Simulation;
import simulation.StepResult;


public class FloodingAlgorithm extends RoutingAlgorithm {

    @Override
    public void buildRoutingTables(Topology topology) {
	return;
    }

    @Override
    public StepResult step(Topology topology) {
	StepResult result = new StepResult(Simulation.getInstance()
		.getStepNumber());

	// Refresh packets, dropping repeats
	for (Router router : topology) {
	    List<Packet> packetsDropped = router.dropRepeatedPackets();
	    Map<String, List<Packet>> packetsReceived = router.refreshPackets();

	    for (Packet packet : packetsDropped) {
		result.addPacketDroppedMessage(packet);
	    }

	    for (Packet packet : packetsReceived.get("removed")) {
		result.addPacketReceivedMessage(packet);
	    }

	    for (Packet packet : packetsReceived.get("dropped")) {
		result.addPacketDroppedMessage(packet);
	    }
	}

	for (Router source : topology) {
	    for (Packet packet : source.getNonStalePackets()) {
		Deque<Router> nextRouters = new ArrayDeque<>();
		for (Router connection : source.getConnections()) {
		    if (!packet.hasVisited(connection)) {
			nextRouters.push(connection);
		    }
		}

		if (nextRouters.size() == 0) {
		    source.dropPacket(packet);
		    result.addPacketDroppedMessage(packet);
		} else {
		    while (nextRouters.size() > 1) {
			Packet copy = new Packet(packet);
			topology.injectPacketCopy(copy, source);
			source.sendPacket(copy, nextRouters.pop());
			result.addPacketSentMessage(copy, source);
		    }
		    source.sendPacket(packet, nextRouters.pop());
		    result.addPacketSentMessage(packet, source);
		}
	    }
	}

	return result;
    }

    @Override
    public String toString() {
	return "Flooding";
    }

}
