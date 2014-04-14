package roverMock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class RoverForm extends JFrame {

	static RoverForm frame;
	private JPanel contentPane;
	JTextArea SerialHistoryLbl;
	private JTabbedPane tabbedPane;
	private JPanel MovementPnl;
	private JPanel TemperaturePnl;
	private JPanel BatteryPnl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new RoverForm();
					RoverCode.align();
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setLocation((int)(screenSize.getWidth() - frame.getWidth() - 20), 20);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RoverForm() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				RoverEvents.Window_Opened();
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(RoverForm.class.getResource("/Tire.png")));
		setTitle("Mock Rover");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Serial", null, scrollPane, null);
		
		SerialHistoryLbl = new JTextArea();
		scrollPane.setViewportView(SerialHistoryLbl);
		SerialHistoryLbl.setOpaque(false);
		SerialHistoryLbl.setEditable(false);
		SerialHistoryLbl.setFont(new Font("Bookman Old Style", Font.PLAIN, 13));
		
		MovementPnl = new JPanel();
		tabbedPane.addTab("Movement", null, MovementPnl, null);
		
		TemperaturePnl = new JPanel();
		tabbedPane.addTab("Temperature", null, TemperaturePnl, null);
		
		BatteryPnl = new JPanel();
		tabbedPane.addTab("Battery", null, BatteryPnl, null);
	}

}