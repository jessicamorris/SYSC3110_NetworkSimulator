package network;

import java.util.ArrayList;
import java.util.List;

public class Edge implements Comparable<Edge> {

    private final Router source;
    private final Router destination;
    private final int weight;
    private final List<Packet> sourceBuffer;
    private final List<Packet> destinationBuffer;

    public Edge(Router source, Router destination) {
	this(source, destination, -1);
    }

    public Edge(Router source, Router destination, int weight) {
	this.source = source;
	this.destination = destination;
	this.weight = weight;

	sourceBuffer = new ArrayList<>();
	destinationBuffer = new ArrayList<>();
    }

    public int getWeight() {
	return weight;
    }

    public Router getNeighbour(Router router) {
	if (router == source)
	    return destination;
	else if (router == destination)
	    return source;
	else {
	    throw new IllegalArgumentException();
	}
    }

    public void bufferPacket(Router router, Packet packet)
	    throws IllegalArgumentException {
	if (router == source) {
	    destinationBuffer.add(packet);
	} else if (router == destination) {
	    sourceBuffer.add(packet);
	} else {
	    throw new IllegalArgumentException();
	}
    }

    public List<Packet> getBufferedPackets(Router router) {
	List<Packet> bufferedPackets;

	if (router == source) {
	    bufferedPackets = new ArrayList<>(sourceBuffer);
	    sourceBuffer.clear();
	} else if (router == destination) {
	    bufferedPackets = new ArrayList<>(destinationBuffer);
	    destinationBuffer.clear();
	} else {
	    throw new IllegalArgumentException();
	}
	return bufferedPackets;
    }

    @Override
    public int compareTo(Edge edge) {
	if (edge == null)
	    throw new NullPointerException();
	if (this == edge)
	    return 0;

	return source.compareTo(edge.source)
		+ destination.compareTo(edge.destination);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Edge other = (Edge) obj;
	if (destination == null) {
	    if (other.destination != null)
		return false;
	} else if (!destination.equals(other.destination))
	    return false;
	if (source == null) {
	    if (other.source != null)
		return false;
	} else if (!source.equals(other.source))
	    return false;
	return true;
    }

}
