package simulatorWrapper;

import interfaceGUI.InterfaceEvents;

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
import javax.swing.JSlider;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JToggleButton;

import roverMock.RoverEvents;
import satelliteMock.SatelliteEvents;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WrapperForm extends JFrame {

	static WrapperForm frame;
	private JPanel contentPane;
	
	JLabel RoverBufferLbl;
	JLabel SatelliteBufferLbl;
	JLabel GroundBufferLbl;
	JSlider TimeSlider;
	JLabel RoverAvailableLbl;
	JLabel SatelliteAvailableLbl;
	JLabel GroundAvailableLbl;
	JToggleButton ViewGUITgl;
	JToggleButton ViewMapTgl;
	JToggleButton ViewRoverTgl;
	JToggleButton ViewSatTgl;

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
		setResizable(false);
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
		RoverBufferLbl.setBounds(87, 38, 463, 20);
		contentPane.add(RoverBufferLbl);
		
		SatelliteBufferLbl = new JLabel("");
		SatelliteBufferLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
		SatelliteBufferLbl.setBorder(new LineBorder(new Color(0, 0, 0)));
		SatelliteBufferLbl.setBounds(87, 64, 463, 20);
		contentPane.add(SatelliteBufferLbl);
		
		GroundBufferLbl = new JLabel("");
		GroundBufferLbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GroundBufferLbl.setBorder(new LineBorder(new Color(0, 0, 0)));
		GroundBufferLbl.setBounds(87, 89, 463, 20);
		contentPane.add(GroundBufferLbl);
		
		JLabel lblTimeRate = new JLabel("Time Rate");
		lblTimeRate.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
		lblTimeRate.setBounds(10, 131, 107, 19);
		contentPane.add(lblTimeRate);
		
		TimeSlider = new JSlider();
		TimeSlider.setMaximum(70);
		TimeSlider.setValue(30);
		TimeSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				WrapperEvents.TimeSliderValue_Changed();
			}
		});
		TimeSlider.setMajorTickSpacing(10);
		TimeSlider.setMinorTickSpacing(1);
		TimeSlider.setPaintTicks(true);
		TimeSlider.setSnapToTicks(true);
		TimeSlider.setBounds(20, 161, 556, 34);
		contentPane.add(TimeSlider);
		
		JLabel label = new JLabel("  1/8            1/4             1/2               1                 2                 4                  8               16");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		label.setBounds(10, 200, 576, 14);
		contentPane.add(label);
		
		RoverAvailableLbl = new JLabel("0");
		RoverAvailableLbl.setHorizontalAlignment(SwingConstants.CENTER);
		RoverAvailableLbl.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		RoverAvailableLbl.setBounds(560, 37, 23, 23);
		contentPane.add(RoverAvailableLbl);
		
		SatelliteAvailableLbl = new JLabel("0");
		SatelliteAvailableLbl.setHorizontalAlignment(SwingConstants.CENTER);
		SatelliteAvailableLbl.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		SatelliteAvailableLbl.setBounds(560, 62, 23, 23);
		contentPane.add(SatelliteAvailableLbl);
		
		GroundAvailableLbl = new JLabel("0");
		GroundAvailableLbl.setHorizontalAlignment(SwingConstants.CENTER);
		GroundAvailableLbl.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
		GroundAvailableLbl.setBounds(560, 87, 23, 23);
		contentPane.add(GroundAvailableLbl);
		
		JLabel lblAvailable = new JLabel("Available:");
		lblAvailable.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAvailable.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
		lblAvailable.setBounds(519, 20, 67, 14);
		contentPane.add(lblAvailable);
		
		JLabel lblViewMultipleWindows = new JLabel("View Multiple Windows");
		lblViewMultipleWindows.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
		lblViewMultipleWindows.setBounds(10, 244, 183, 19);
		contentPane.add(lblViewMultipleWindows);
		
		ViewGUITgl = new JToggleButton("Interface");
		ViewGUITgl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RoverEvents.setGUIVisible(ViewGUITgl.isSelected());
			}
		});
		ViewGUITgl.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
		ViewGUITgl.setSelected(true);
		ViewGUITgl.setBounds(20, 274, 107, 23);
		contentPane.add(ViewGUITgl);
		
		ViewMapTgl = new JToggleButton("Map");
		ViewMapTgl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MapFrame.frame.setVisible(ViewMapTgl.isSelected());
			}
		});
		ViewMapTgl.setSelected(true);
		ViewMapTgl.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
		ViewMapTgl.setBounds(137, 274, 107, 23);
		contentPane.add(ViewMapTgl);
		
		ViewRoverTgl = new JToggleButton("Rover");
		ViewRoverTgl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InterfaceEvents.setGUIVisible(ViewRoverTgl.isSelected());
			}
		});
		ViewRoverTgl.setSelected(true);
		ViewRoverTgl.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
		ViewRoverTgl.setBounds(254, 275, 107, 23);
		contentPane.add(ViewRoverTgl);
		
		ViewSatTgl = new JToggleButton("Satellite");
		ViewSatTgl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SatelliteEvents.setGUIVisible(ViewSatTgl.isSelected());
			}
		});
		ViewSatTgl.setSelected(true);
		ViewSatTgl.setFont(new Font("Bookman Old Style", Font.PLAIN, 12));
		ViewSatTgl.setBounds(371, 274, 107, 23);
		contentPane.add(ViewSatTgl);
	}
}
