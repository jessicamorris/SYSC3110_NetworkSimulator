package algorithm;

import java.util.List;
import java.util.Map;

import network.Packet;
import network.Router;
import network.RoutingTable;
import network.Topology;
import simulation.Simulation;
import simulation.StepResult;


public class ShortestPathAlgorithm extends RoutingAlgorithm {

    @Override
    public void buildRoutingTables(Topology topology) {
	// Building routing tables using Dijkstra's shortest path algorithm
	for (Router router : topology) {
	    RoutingTable routingTable = new RoutingTable(topology, router);
	    router.setRoutingTable(routingTable);
	}
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
		Router next = source.getNextHop(packet.getDestination());
		source.sendPacket(packet, next);

		result.addPacketSentMessage(packet, source);
	    }
	}

	return result;
    }

    @Override
    public String toString() {
	return "Shortest Path";
    }

}
