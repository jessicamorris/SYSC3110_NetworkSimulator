package algorithm;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import network.Packet;
import network.Router;
import network.Topology;
import simulation.Simulation;
import simulation.StepResult;


public class RandomPathAlgorithm extends RoutingAlgorithm {

    @Override
    public void buildRoutingTables(Topology topology) {
	return;
    }

    @Override
    public StepResult step(Topology topology) {
	StepResult result = new StepResult(Simulation.getInstance()
		.getStepNumber());

	// Refresh packets
	for (Router router : topology) {
	    Map<String, List<Packet>> packetsReceived = router.refreshPackets();
	    for (Packet packet : packetsReceived.get("removed")) {
		result.addPacketReceivedMessage(packet);
	    }
	    for (Packet packet : packetsReceived.get("dropped")) {
		result.addPacketDroppedMessage(packet);
	    }
	}

	// Send packets
	for (Router source : topology) {
	    for (Packet packet : source.getNonStalePackets()) {
		Router next = null;
		Set<Router> connections = source.getConnections();
		int count = 0;
		int destIndex = ThreadLocalRandom.current().nextInt(0,
			connections.size());

		for (Router neighbour : connections) {
		    if (count == destIndex) {
			next = neighbour;
			break;
		    } else
			count++;
		}
		source.sendPacket(packet, next);
		result.addPacketSentMessage(packet, source);
	    }
	}

	return result;
    }

    @Override
    public String toString() {
	return "Random Path";
    }

}
