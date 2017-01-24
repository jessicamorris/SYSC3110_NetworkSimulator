package gui.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import network.Packet;

@SuppressWarnings("serial")
public class PacketTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = { "Packet ID", "Source",
	    "Destination", "Hops" };
    private static final Class<?>[] COLUMN_CLASSES = { String.class,
	    String.class, String.class, String.class };

    private final List<Packet> packets;

    public PacketTableModel(List<Packet> packets) {
	this.packets = packets;
    }

    @Override
    public int getColumnCount() {
	return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
	return packets.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
	Packet packet = packets.get(row);

	if (column == 0) {
	    return packet.getID();
	} else if (column == 1) {
	    return packet.getSource().getName();
	} else if (column == 2) {
	    return packet.getDestination().getName();
	} else if (column == 3) {
	    return packet.getHops() + "";
	}
	return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
	return false;
    }

    @Override
    public String getColumnName(int column) {
	return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
	return COLUMN_CLASSES[column];
    }

}
