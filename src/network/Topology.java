package network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import common.TopologyUpdateEvent;
import common.TopologyUpdateEvent.TopologyUpdate;

public class Topology extends Observable implements Iterable<Router> {

    private final Set<Router> network;
    private int edgeCount;

    public Topology() {
	network = new TreeSet<>();
	edgeCount = 0;
    }

    public Set<Router> getNetwork() {
	return network;
    }

    public boolean addRouter(Router router) {
	if (!network.add(router)) {
	    return false;
	} else {
	    setChanged();
	    notifyObservers(new TopologyUpdateEvent(
		    TopologyUpdate.ROUTER_ADDED, router, network.size()));
	    return true;
	}
    }

    public boolean removeRouter(Router router) {
	if (network.remove(router)) {
	    for (Router neighbour : router.getConnections()) {
		neighbour.disconnectFrom(router);
	    }

	    setChanged();
	    notifyObservers(new TopologyUpdateEvent(
		    TopologyUpdate.ROUTER_REMOVED, router, network.size()));
	    return true;
	} else {
	    return false;
	}
    }

    public void addEdge(Router router1, Router router2) {
	if (router1.connectTo(router2)) {
	    edgeCount++;
	    setChanged();
	    notifyObservers(new TopologyUpdateEvent(TopologyUpdate.EDGE_ADDED,
		    new Router[] { router1, router2 }, edgeCount));
	}
    }

    public void removeEdge(Router router1, Router router2) {
	if (router1.disconnectFrom(router2)) {
	    router2.disconnectFrom(router1);
	    edgeCount--;
	    setChanged();
	    notifyObservers(new TopologyUpdateEvent(
		    TopologyUpdate.EDGE_REMOVED, new Router[] { router1,
			    router2 }, edgeCount));
	}
    }

    public int size() {
	return network.size();
    }

    public boolean isEmpty() {
	return network.isEmpty();
    }

    public boolean hasEdges() {
	return edgeCount > 0;
    }

    public boolean isValid() {
	if (network.size() < 2) {
	    return false;
	}

	Set<Router> visited = new HashSet<>();
	visited.add(routerAtIndex(0));
	boolean routersAdded = true;

	while (routersAdded) {
	    routersAdded = false;
	    Collection<Router> alreadyVisited = new HashSet<>(visited);
	    for (Router router : alreadyVisited) {
		routersAdded |= visited.addAll(router.getConnections());
	    }
	}

	return visited.containsAll(network);
    }

    private Router routerAtIndex(int index) {
	int count = 0;
	for (Router router : network) {
	    if (count == index)
		return router;
	    else
		count++;
	}

	return null;
    }

    public Packet injectPacket(String packetID) {
	int destIndex = ThreadLocalRandom.current().nextInt(0, network.size());
	Router dest = routerAtIndex(destIndex);

	int srcIndex;
	do {
	    srcIndex = ThreadLocalRandom.current().nextInt(0, network.size());
	} while (srcIndex == destIndex);
	Router src = routerAtIndex(srcIndex);

	Packet packet = new Packet(packetID, src, dest);
	src.addPacket(packet);

	setChanged();
	notifyObservers(new TopologyUpdateEvent(TopologyUpdate.PACKET_INJECTED,
		packet));

	return packet;
    }

    public void injectPacketCopy(Packet packet, Router router) {
	router.addPacket(packet);

	setChanged();
	notifyObservers(new TopologyUpdateEvent(TopologyUpdate.PACKET_INJECTED,
		packet));
    }

    public void resetPackets() {
	for (Router router : network) {
	    router.resetPackets();
	}
    }

    public Object[] toArray() {
	return network.toArray();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void forEach(Consumer consumer) {
	network.forEach(consumer);
    }

    @Override
    public Iterator<Router> iterator() {
	return network.iterator();
    }

    @Override
    public Spliterator<Router> spliterator() {
	return network.spliterator();
    }
}
