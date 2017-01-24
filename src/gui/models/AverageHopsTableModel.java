package gui.models;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import network.Packet;
import network.Router;
import network.Topology;
import simulation.Simulation;

import common.SimulationUpdateEvent;
import common.SimulationUpdateEvent.SimulationUpdate;
import common.TopologyUpdateEvent;
import common.TopologyUpdateEvent.TopologyUpdate;

@SuppressWarnings("serial")
public class AverageHopsTableModel extends AbstractTableModel implements
	Observer {

    // Constants
    private static final String[] COLUMN_NAMES = { "Source", "Destination",
	    "Average Hops" };
    private static final Class<?>[] COLUMN_CLASSES = { String.class,
	    String.class, String.class };

    private final Map<Router, Map<Router, AverageTuple>> averageHops;

    public AverageHopsTableModel() {
	averageHops = new TreeMap<>();

	Simulation.getInstance().addObserver(this);
    }

    @Override
    public int getColumnCount() {
	return COLUMN_CLASSES.length;
    }

    @Override
    public int getRowCount() {
	int rowCount = 0;

	for (Router source : averageHops.keySet()) {
	    rowCount += averageHops.get(source).size();
	}

	return rowCount;
    }

    @Override
    public Object getValueAt(int row, int column) {
	if (column == 0) {
	    return getSourceAt(row).toString();
	} else if (column == 1) {
	    return getDestinationAt(row).toString();
	} else if (column == 2) {
	    DecimalFormat df = new DecimalFormat();
	    df.setMinimumFractionDigits(1);
	    df.setMaximumFractionDigits(5);
	    return df.format(getAverageHops(getSourceAt(row),
		    getDestinationAt(row)));
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

    private Router getSourceAt(int row) {
	int rowCount = 0;
	for (Router source : averageHops.keySet()) {
	    rowCount += averageHops.get(source).size();

	    if (rowCount > row) {
		return source;
	    }
	}
	return null;
    }

    private void clear() {
	if (getRowCount() > 0) {
	    int rows = getRowCount() - 1;
	    averageHops.clear();
	    fireTableRowsDeleted(0, rows);
	}
    }

    private int getRowOf(Router source, Router destination) {
	int rowCount = 0;

	for (Router router : averageHops.keySet()) {
	    if (router == source) {
		break;
	    } else {
		rowCount += averageHops.get(router).size();
	    }
	}

	for (Router router : averageHops.get(source).keySet()) {
	    if (router == destination) {
		break;
	    } else {
		rowCount++;
	    }
	}

	return rowCount;
    }

    private Router getDestinationAt(int row) {
	int rowCount = 0;
	Router source = null;
	for (Router router : averageHops.keySet()) {
	    rowCount += averageHops.get(router).size();

	    if (rowCount > row) {
		source = router;
		break;
	    }
	}

	if (source != null) {
	    rowCount -= averageHops.get(source).size();
	    for (Router destination : averageHops.get(source).keySet()) {
		if (rowCount - row == 0) {
		    return destination;
		}
		rowCount++;
	    }
	}

	return null;
    }

    private double getAverageHops(Router source, Router destination) {
	return averageHops.get(source).get(destination).getAverage();
    }

    private void updateAverageHops(Router source, Router destination,
	    long newHops) {
	if (!averageHops.containsKey(source)) {
	    averageHops.put(source, new TreeMap<Router, AverageTuple>());
	}

	if (!averageHops.get(source).containsKey(destination)) {
	    averageHops.get(source).put(destination, new AverageTuple(0, 0));
	    int rowNum = getRowOf(source, destination);
	    fireTableRowsInserted(rowNum, rowNum);
	}

	averageHops.get(source).get(destination).addHops(newHops);
	fireTableCellUpdated(getRowOf(source, destination), 2);
    }

    @Override
    public void update(Observable observable, Object update) {

	if (observable instanceof Simulation) {
	    SimulationUpdateEvent event = (SimulationUpdateEvent) update;
	    if (event.getAction() == SimulationUpdate.METRICS_RESET) {
		clear();
	    }
	} else if (observable instanceof Topology) {
	    TopologyUpdateEvent event = (TopologyUpdateEvent) update;
	    if (event.getAction() == TopologyUpdate.PACKET_INJECTED) {
		Packet packet = event.getPacket();
		packet.addObserver(this);
	    }
	} else if (observable instanceof Packet) {
	    Packet packet = (Packet) observable;
	    updateAverageHops(packet.getSource(), packet.getDestination(),
		    packet.getHops());
	}
    }

    private class AverageTuple {
	public long sum;
	public int size;

	public AverageTuple(long sum, int size) {
	    this.sum = sum;
	    this.size = size;
	}

	public double getAverage() {
	    return ((sum + 0.0) / size);
	}

	public void addHops(long hops) {
	    sum += hops;
	    size++;
	}
    }

}
