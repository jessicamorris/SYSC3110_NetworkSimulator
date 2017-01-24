package simulation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import network.Packet;
import network.Router;

public class StepResult {

    private final BigInteger stepNumber;
    private BigInteger packetsTransmitted;
    private final List<String> packetMessages;

    public StepResult(BigInteger stepNumber) {
	this.stepNumber = stepNumber;
	packetsTransmitted = BigInteger.ZERO;
	packetMessages = new ArrayList<>();
    }

    public BigInteger getStepNumber() {
	return stepNumber;
    }

    public BigInteger getPacketsTransmitted() {
	return packetsTransmitted;
    }

    public List<String> getPacketMessages() {
	return packetMessages;
    }

    public void addPacketReceivedMessage(Packet packet) {
	packetMessages.add(new String("P" + packet.getID()
		+ " reached its destination "
		+ packet.getDestination().getName() + "."));
    }

    public void addPacketSentMessage(Packet packet, Router source) {
	packetMessages
		.add(new String(source.getName() + " sent P" + packet.getID()
			+ " to " + packet.getCurrentRouter().getName()));
	packetsTransmitted = packetsTransmitted.add(BigInteger.ONE);
    }

    public void addPacketInjectedMessage(Packet packet) {
	packetMessages.add(new String("P" + packet.getID() + " added to "
		+ packet.getSource().getName()));
    }

    public void addPacketDroppedMessage(Packet packet) {
	packetMessages.add(new String(packet.getCurrentRouter().getName()
		+ " dropped P" + packet.getID()));
    }

    @Override
    public String toString() {
	return toHTML();
    }

    private String toHTML() {
	StringBuilder sb = new StringBuilder("<b>Step " + stepNumber.toString()
		+ ":</b><ul style=\"margin-left:5px; list-style-type:none;\">");
	for (String message : packetMessages) {
	    sb.append("<li>" + message + "</li>");
	}
	sb.append("</ul>");

	if (stepNumber.compareTo(BigInteger.ONE) > 0) {
	    sb.append("<hr style=\"display: inline-block; width:80%;\">");
	}

	return sb.toString();
    }
}
