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
import objects.DecimalPoint;

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
	private JRadioButtonMenuItem rdbtnmntmBlueToWhite;
	private int panShiftX = 0;
	private int panShiftY = 0;

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
				rdbtnmntmBlueToWhite.setSelected(false);
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
				rdbtnmntmBlueToWhite.setSelected(false);
				rdbtnmntmBlackToWhite.setSelected(true);
				PlasmaMap.setColorScheme(PlasmaPanel.BLACKtoWHITE);
			}
		});
		mnColorScheme.add(rdbtnmntmBlackToWhite);
		
		rdbtnmntmBlueToWhite = new JRadioButtonMenuItem("Blue to White");
		rdbtnmntmBlueToWhite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnmntmRedToGreen.setSelected(false);
				rdbtnmntmBlackToWhite.setSelected(false);
				rdbtnmntmBlueToWhite.setSelected(true);
				PlasmaMap.setColorScheme(PlasmaPanel.BLUEtoWHITE);
			}
		});
		mnColorScheme.add(rdbtnmntmBlueToWhite);
		
		JMenu mnMap = new JMenu("Map");
		menuBar.add(mnMap);
		
		JMenu mnLoadMap = new JMenu("Load Map");
		mnMap.add(mnLoadMap);
		
		JMenuItem mntmPlasmaMap = new JMenuItem("Plasma Map");
		mntmPlasmaMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapValues = PlasmaMap.genorateLandscape(8, 0.05);
				mapTargets = PlasmaMap.genorateTargets();
				PlasmaMap.setTargets(mapTargets);
			}
		});
		mnLoadMap.add(mntmPlasmaMap);
		
		JMenuItem mntmFlatMap = new JMenuItem("Flat Map");
		mntmFlatMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadFlatMap();
			}
		});
		mnLoadMap.add(mntmFlatMap);
		
		JMenuItem mntmFromFile = new JMenuItem("From File...");
		mnLoadMap.add(mntmFromFile);
		
		JMenuItem mntmScaleTerrainVertical = new JMenuItem("Scale Terrain Vertical");
		mnMap.add(mntmScaleTerrainVertical);
		
		JMenuItem mntmResetGenorateTargets = new JMenuItem("Regenorate Targets");
		mntmResetGenorateTargets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mapTargets = PlasmaMap.genorateTargets();
				PlasmaMap.setTargets(mapTargets);
			}
		});
		mnMap.add(mntmResetGenorateTargets);
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
		mapValues = PlasmaMap.genorateLandscape(7, 0.05);
		mapTargets = PlasmaMap.genorateTargets();
		PlasmaMap.setTargets(mapTargets);
		contentPane.add(PlasmaMap);
		PlasmaMap.setLocation(107, 44);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Integer key = e.getKeyCode();
				switch (key) {
				case 39:
					panShiftX = panShiftX - PlasmaMap.getResolution();
					redraw();
					break;
				case 37:
					panShiftX = panShiftX + PlasmaMap.getResolution();
					redraw();
					break;
				case 38:
					panShiftY = panShiftY + PlasmaMap.getResolution();
					redraw();
					break;
				case 40:
					panShiftY = panShiftY - PlasmaMap.getResolution();
					redraw();
					break;
				case 32:
					panShiftX = 0;
					panShiftY = 0;
					redraw();
					break;
				}
			}
		});
	}
	
	private void resized(){
		redraw();
		setRoverLocation(roverLocation);
	}

	public void redraw(){
		RoverMarker.setLocation((this.getWidth() / 2 + panShiftX), (this.getHeight() / 2 + panShiftY));
		Point pointOnScreen = roverLocation.scale(PlasmaMap.getResolution()/100.0).toPoint();
		PlasmaMap.setLocation((int)((this.getWidth() - PlasmaMap.getWidth()) / 2 - pointOnScreen.getX()) + panShiftX, (int)((this.getHeight() - PlasmaMap.getHeight()) / 2 + pointOnScreen.getY()) + panShiftY);
	}
	
	public void setRoverLocation(DecimalPoint loc){
		roverLocation = loc;
		redraw();
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
		panShiftX = 0;
		panShiftY = 0;
		PlasmaMap.setResolution(PlasmaMap.getResolution() + scale);
		setRoverLocation(roverLocation);
		redraw();
	}
	
	private void loadFlatMap(){
		mapValues = new double[300][300];
		int x = 0;
		while (x < mapValues.length){
			int y = 0;
			while (y < mapValues[0].length){
				mapValues[x][y] = 2;
				y++;
			}
			x++;
		}
		mapValues[0][0] = 0;
		mapValues[mapValues.length-1][mapValues[0].length-1] = 3;
		mapTargets = PlasmaMap.genorateTargets();
		PlasmaMap.setTargets(mapTargets);
		PlasmaMap.setValues(mapValues);
	}
	
	public double getIncline(){
		//TODO incline calculation
		return 0;
	}
}
