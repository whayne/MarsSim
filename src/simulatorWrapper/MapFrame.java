package simulatorWrapper;

import interfaceGUI.InterfaceForm;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import objects.BackgroundImage;

import java.awt.Toolkit;
import javax.swing.JLabel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class MapFrame extends JFrame {

	static MapFrame frame;
	private JPanel contentPane;
	public MapIcon RoverMarker;
	public PlasmaPanel PlasmaMap;
	public double[][] mapValues;
	public Point[] mapTargets;
	private DecimalPoint roverLocation = new DecimalPoint();
	private JRadioButtonMenuItem rdbtnmntmRedToGreen;
	private JRadioButtonMenuItem rdbtnmntmBlackToWhite;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new MapFrame();
					WrapperMain.alignMap();
					frame.setLocation(300, 200);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MapFrame() {
		initalize();	
		contentPane.setComponentZOrder(PlasmaMap, contentPane.getComponentCount() - 1);
		setBounds(400, 100, 482, 374);
	}
	
	private void initalize(){
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				resized();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDisplay = new JMenu("Display");
		menuBar.add(mnDisplay);
		
		JMenu mnZoom = new JMenu("Zoom");
		mnDisplay.add(mnZoom);
		
		JMenuItem mntmZoomIn = new JMenuItem("Zoom In");
		mntmZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoom(5);
			}
		});
		mnZoom.add(mntmZoomIn);
		
		JMenuItem mntmZoomOut = new JMenuItem("Zoom Out");
		mntmZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoom(-5);
			}
		});
		mnZoom.add(mntmZoomOut);
		
		JMenu mnColorScheme = new JMenu("Color Scheme");
		mnDisplay.add(mnColorScheme);
		
		rdbtnmntmRedToGreen = new JRadioButtonMenuItem("Red to Green");
		rdbtnmntmRedToGreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnmntmBlackToWhite.setSelected(false);
				rdbtnmntmRedToGreen.setSelected(true);
				PlasmaMap.setColorScheme(PlasmaPanel.REDtoGREEN);
			}
		});
		rdbtnmntmRedToGreen.setSelected(true);
		mnColorScheme.add(rdbtnmntmRedToGreen);
		
		rdbtnmntmBlackToWhite = new JRadioButtonMenuItem("Black to White");
		rdbtnmntmBlackToWhite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnmntmRedToGreen.setSelected(false);
				rdbtnmntmBlackToWhite.setSelected(true);
				PlasmaMap.setColorScheme(PlasmaPanel.BLACKtoWHITE);
			}
		});
		mnColorScheme.add(rdbtnmntmBlackToWhite);
		
		JMenu mnMap = new JMenu("Map");
		menuBar.add(mnMap);
		
		JMenu mnLoadMap = new JMenu("Load Map");
		mnMap.add(mnLoadMap);
		
		JMenuItem mntmPlasmaMap = new JMenuItem("Plasma Map");
		mnLoadMap.add(mntmPlasmaMap);
		
		JMenuItem mntmFlatMap = new JMenuItem("Flat Map");
		mnLoadMap.add(mntmFlatMap);
		
		JMenuItem mntmFromFile = new JMenuItem("From File...");
		mnLoadMap.add(mntmFromFile);
		
		JMenuItem mntmScaleTerrainVertical = new JMenuItem("Scale Terrain Vertical");
		mnMap.add(mntmScaleTerrainVertical);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MapFrame.class.getResource("/Map_with_Compass.png")));
		setTitle("Rover Movement Viewer");
		contentPane.setLayout(null);
		
		RoverMarker = new MapIcon();
		RoverMarker.setLocation(198, 162);
		RoverMarker.setSize(60, 60);
		RoverMarker.setAngle(90);
		contentPane.add(RoverMarker);
		
		PlasmaMap = new PlasmaPanel();
		mapValues = PlasmaMap.genorateLandscape(7, 0.1);
		mapTargets = PlasmaMap.genorateTargets();
		PlasmaMap.setTargets(mapTargets);
		contentPane.add(PlasmaMap);
		PlasmaMap.setLocation(107, 44);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//System.out.println("Keys");
				Integer key = e.getKeyCode();
				switch (key) {
				case 39:
					setRoverLocation(roverLocation.offset(10, 0));
					//PlasmaMap.setLocation(PlasmaMap.getX() - 10, PlasmaMap.getY());
					RoverMarker.setAngle(0);
					break;
				case 37:
					setRoverLocation(roverLocation.offset(-10, 0));
					//PlasmaMap.setLocation(PlasmaMap.getX() + 10, PlasmaMap.getY());
					RoverMarker.setAngle(180);
					break;
				case 38:
					setRoverLocation(roverLocation.offset(0, 10));
					//PlasmaMap.setLocation(PlasmaMap.getX(), PlasmaMap.getY() + 10);
					RoverMarker.setAngle(90);
					break;
				case 40:
					setRoverLocation(roverLocation.offset(0, -10));
					//PlasmaMap.setLocation(PlasmaMap.getX(), PlasmaMap.getY() - 10);
					RoverMarker.setAngle(270);
					break;
				}
			}
		});
		
	}
	
	private void resized(){
		RoverMarker.setLocation((this.getWidth() / 2), (this.getHeight() / 2));
		setRoverLocation(roverLocation);
	}

	public void setRoverLocation(DecimalPoint loc){
		roverLocation = loc;
		Point pointOnScreen = roverLocation.scale(PlasmaMap.getResolution()/100.0).toPoint();
		PlasmaMap.setLocation((int)((this.getWidth() - PlasmaMap.getWidth()) / 2 - pointOnScreen.getX()), (int)((this.getHeight() - PlasmaMap.getHeight()) / 2 + pointOnScreen.getY()));
	}
	
	public DecimalPoint getRoverLocation(){
		return roverLocation;
	}
	
	public void setRoverDirection(double direction){
		RoverMarker.setAngle(direction);
	}
	
	public double getRoverDirection(){
		return RoverMarker.getAngle();
	}
	
	private void zoom(int scale){
		PlasmaMap.setResolution(PlasmaMap.getResolution() + scale);
		setRoverLocation(roverLocation);
		this.repaint();	
	}
}
