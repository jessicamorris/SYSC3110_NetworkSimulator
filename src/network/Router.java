package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Router implements Comparable<Router> {

    private final String name;
    private final Set<Router> connections;
    private final List<Packet> packets;
    private final Set<String> seenPacketIDs;
    private RoutingTable routingTable;

    public Router(String name) throws IllegalArgumentException {
	if (name.length() == 0) {
	    throw new IllegalArgumentException();
	}

	this.name = name;
	connections = new HashSet<>();
	packets = new ArrayList<>();
	seenPacketIDs = new HashSet<>();
    }

    public String getName() {
	return name;
    }

    public Set<Router> getConnections() {
	return connections;
    }

    public List<Packet> getPackets() {
	return packets;
    }

    public void setRoutingTable(RoutingTable routingTable) {
	this.routingTable = routingTable;
    }

    public Router getNextHop(Router destination) {
	return routingTable.getNext(destination);
    }

    public boolean connectTo(Router router) {
	return connections.add(router) && router.connections.add(this);
    }

    public boolean disconnectFrom(Router router) {
	return connections.remove(router);
    }

    public boolean isConnectedTo(Router router) {
	return connections.contains(router);
    }

    public List<Packet> getNonStalePackets() {
	List<Packet> nonStalePackets = new ArrayList<>();
	for (Packet packet : packets) {
	    if (!packet.isStale())
		nonStalePackets.add(packet);
	}

	return nonStalePackets;
    }

    private List<Packet> getStalePackets() {
	List<Packet> stalePackets = new ArrayList<>();
	for (Packet packet : packets) {
	    if (packet.isStale()) {
		stalePackets.add(packet);
	    }
	}

	return stalePackets;
    }

    protected void addPacket(Packet packet) {
	packets.add(packet);
    }

    public void dropPacket(Packet packet) {
	if (packets.contains(packet)) {
	    packets.remove(packet);
	}
    }

    public void sendPacket(Packet packet, Router destination) {
	packets.remove(packet);
	packet.sendTo(destination);
    }

    public List<Packet> dropRepeatedPackets() {
	List<Packet> droppedPackets = new ArrayList<>();

	for (Packet packet : packets) {
	    if (seenPacketIDs.contains(packet.getID())) {
		droppedPackets.add(packet);
	    }
	}
	packets.removeAll(droppedPackets);

	return droppedPackets;
    }

    public Map<String, List<Packet>> refreshPackets() {
	Map<String, List<Packet>> result = new HashMap<>();
	List<Packet> removedPackets = new ArrayList<>();
	List<Packet> droppedPackets = new ArrayList<>();

	for (Packet packet : packets) {
	    if (packet.destinationReached()) {
		if (seenPacketIDs.contains(packet.getID())) {
		    droppedPackets.add(packet);
		} else {
		    removedPackets.add(packet);
		}
	    } else {
		packet.unstale();
	    }

	    seenPacketIDs.add(packet.getID());
	}

	// TODO: Make this way less junk-y
	packets.removeAll(droppedPackets);
	result.put("dropped", droppedPackets);
	packets.removeAll(removedPackets);
	result.put("removed", removedPackets);
	return result;
    }

    public void unsendPackets() {
	for (Packet packet : getStalePackets()) {
	    packets.remove(packet);
	    seenPacketIDs.remove(packet.getID());
	    packet.unsend();
	    packet.getCurrentRouter().seenPacketIDs.remove(packet.getID());
	}
    }

    protected void resetPackets() {
	packets.clear();
	seenPacketIDs.clear();
    }

    @Override
    public int compareTo(Router router) {
	if (router == null)
	    throw new NullPointerException();
	if (this == router)
	    return 0;

	return name.compareTo(router.name);
    }

    @Override
    public String toString() {
	return name;
    }

}
