package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

    public MainWindow() {
	super("Network Simulator");
	setJMenuBar(new MainMenuBar());
	setMinimumSize(new Dimension(800, 500));
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	setLayout(new BorderLayout());

	add(new StepsPanel(), BorderLayout.WEST);

	JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.addTab("Topology", new TopologyPanel());
	tabbedPane.addTab("Metrics", new MetricsPanel());
	add(tabbedPane, BorderLayout.CENTER);

	pack();
	setVisible(true);
    }

    public static void main(String[] args) {
	new MainWindow();
    }

}
