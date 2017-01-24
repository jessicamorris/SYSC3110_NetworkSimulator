package gui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class CenteredJTable extends JTable {

    public CenteredJTable(TableModel tableModel) {
	super(tableModel);
	setSelectionModel(new DefaultListSelectionModel() {
	    @Override
	    public void setSelectionInterval(int index0, int index1) {
		super.setSelectionInterval(-1, -1);
	    }
	});

	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	renderer.setHorizontalAlignment(SwingConstants.CENTER);
	for (int i = 0; i < tableModel.getColumnCount(); i++) {
	    setDefaultRenderer(tableModel.getColumnClass(i), renderer);
	}
    }
}
