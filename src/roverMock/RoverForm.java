package roverMock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextArea;

public class RoverForm extends JFrame {

	static RoverForm frame;
	private JPanel contentPane;
	JTextArea SerialHistoryLbl;

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

	/**
	 * Create the frame.
	 */
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
		
		SerialHistoryLbl = new JTextArea();
		SerialHistoryLbl.setOpaque(false);
		SerialHistoryLbl.setEditable(false);
		contentPane.add(SerialHistoryLbl, BorderLayout.CENTER);
	}

}