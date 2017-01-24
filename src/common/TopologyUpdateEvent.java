package common;

import network.Packet;
import network.Router;

public class TopologyUpdateEvent {

    public static enum TopologyUpdate {
	ROUTER_ADDED, ROUTER_REMOVED, EDGE_ADDED, EDGE_REMOVED, PACKET_INJECTED, PACKET_RECEIVED
    }

    private final TopologyUpdate action;
    private final Object update;
    private final int updateCount;

    public TopologyUpdateEvent(TopologyUpdate action, Object update,
	    int updateCount) {
	this.action = action;
	this.update = update;
	this.updateCount = updateCount;
    }

    public TopologyUpdateEvent(TopologyUpdate action, Object update) {
	this.action = action;
	this.update = update;
	updateCount = -1;
    }

    public TopologyUpdate getAction() {
	return action;
    }

    public Router getRouter() {
	return ((Router) update);
    }

    public int getRouterCount() {
	return updateCount;
    }

    public Router[] getRouterPair() {
	return (Router[]) update;
    }

    public int getEdgeCount() {
	return updateCount;
    }

    public Packet getPacket() {
	return (Packet) update;
    }

}
