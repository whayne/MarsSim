package simulatorWrapper;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class WrapperForm extends JFrame {

	static WrapperForm frame;
	private JPanel contentPane;
	
	JLabel RoverBufferLbl;
	JLabel SatelliteBufferLbl;
	JLabel GroundBufferLbl;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new WrapperForm();
					WrapperMain.align();
					frame.setLocation(300, 200);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}	

	public WrapperForm() {
		setAlwaysOnTop(true);
		initalize();
	}
	
	private void initalize(){
		setIconImage(Toolkit.getDefaultToolkit().getImage(WrapperForm.class.getResource("/System Icon.png")));
		setTitle("Rover Systems Simulator");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				WrapperEvents.Window_Opened();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 602, 430);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblSerialBuffers = new JLabel("Serial Buffers");
		lblSerialBuffers.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
		lblSerialBuffers.setBounds(10, 11, 107, 19);
		contentPane.add(lblSerialBuffers);
		
		JLabel lblRover = new JLabel("Rover:");
		lblRover.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRover.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		lblRover.setBounds(10, 41, 67, 14);
		contentPane.add(lblRover);
		
		JLabel lblSatellite = new JLabel("Satellite:");
		lblSatellite.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSatellite.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		lblSatellite.setBounds(10, 66, 67, 14);
		contentPane.add(lblSatellite);
		
		JLabel lblGround = new JLabel("Ground:");
		lblGround.setHorizontalAlignment(SwingConstants.TRAILING);
		lblGround.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		lblGround.setBounds(10, 91, 67, 14);
		contentPane.add(lblGround);
		
		RoverBufferLbl = new JLabel("");
		RoverBufferLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
		RoverBufferLbl.setBorder(new LineBorder(new Color(0, 0, 0)));
		RoverBufferLbl.setBounds(87, 38, 180, 20);
		contentPane.add(RoverBufferLbl);
		
		SatelliteBufferLbl = new JLabel("");
		SatelliteBufferLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
		SatelliteBufferLbl.setBorder(new LineBorder(new Color(0, 0, 0)));
		SatelliteBufferLbl.setBounds(87, 64, 180, 20);
		contentPane.add(SatelliteBufferLbl);
		
		GroundBufferLbl = new JLabel("");
		GroundBufferLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GroundBufferLbl.setBorder(new LineBorder(new Color(0, 0, 0)));
		GroundBufferLbl.setBounds(87, 89, 180, 20);
		contentPane.add(GroundBufferLbl);
	}
}
