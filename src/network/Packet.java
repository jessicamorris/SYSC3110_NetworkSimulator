package network;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Observable;

public class Packet extends Observable {

    private final String packetID;
    private final Router source;
    private final Router destination;
    private long hops;
    private boolean isStale;
    private final Deque<Router> history;

    public Packet(String packetID, Router source, Router destination) {
	this.packetID = packetID;
	this.source = source;
	this.destination = destination;
	isStale = true;
	hops = 0;
	history = new ArrayDeque<>();
	history.push(source);
    }

    public Packet(Packet packet) {
	this.packetID = packet.packetID;
	this.source = packet.source;
	this.destination = packet.destination;
	this.isStale = packet.isStale;
	this.hops = packet.hops;
	this.history = new ArrayDeque<>(packet.history);
    }

    public String getID() {
	return packetID;
    }

    public Router getSource() {
	return source;
    }

    public Router getDestination() {
	return destination;
    }

    public boolean destinationReached() {
	if (getCurrentRouter() == destination) {
	    setChanged();
	    notifyObservers();
	    return true;
	} else {
	    return false;
	}
    }

    public long getHops() {
	return hops;
    }

    public void sendTo(Router router) {
	router.addPacket(this);
	history.push(router);
	hops++;
	isStale = true;
    }

    public void unsend() {
	if (hops > 0) {
	    history.pop();
	    history.peek().addPacket(this);
	    hops--;
	    isStale = false;
	}
    }

    public boolean isStale() {
	return isStale;
    }

    public void unstale() {
	isStale = false;
    }

    public Router getCurrentRouter() {
	return history.peek();
    }

    public boolean hasVisited(Router router) {
	return history.contains(router);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((packetID == null) ? 0 : packetID.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Packet other = (Packet) obj;
	if (packetID == null) {
	    if (other.packetID != null)
		return false;
	} else if (!packetID.equals(other.packetID))
	    return false;
	return true;
    }

}
