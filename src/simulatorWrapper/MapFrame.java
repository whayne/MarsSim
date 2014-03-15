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

public class MapFrame extends JFrame {

	static MapFrame frame;
	private JPanel contentPane;
	public MapIcon RoverMarker;
	public PlasmaPanel PlasmaMap;
	public double[][] mapValues;
	public Point[] mapTargets;
	private Point roverLocation = new Point(0, 0);

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MapFrame.class.getResource("/Map_with_Compass.png")));
		setTitle("Rover Movement Viewer");
		contentPane.setLayout(null);
		
		RoverMarker = new MapIcon();
		RoverMarker.setSize(60, 60);
		RoverMarker.setAngle(90);
		contentPane.add(RoverMarker);
		
		PlasmaMap = new PlasmaPanel();
		mapValues = PlasmaMap.genorateLandscape(7, 0.1);
		mapTargets = PlasmaMap.genorateTargets();
		PlasmaMap.setTargets(mapTargets);
		contentPane.add(PlasmaMap);
		PlasmaMap.setLocation((this.getWidth() - PlasmaMap.getWidth()) / 2, (this.getHeight() - PlasmaMap.getHeight()) / 2);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//System.out.println("Keys");
				Integer key = e.getKeyCode();
				switch (key) {
				case 39:
					PlasmaMap.setLocation(PlasmaMap.getX() - 10, PlasmaMap.getY());
					RoverMarker.setAngle(0);
					break;
				case 37:
					PlasmaMap.setLocation(PlasmaMap.getX() + 10, PlasmaMap.getY());
					RoverMarker.setAngle(180);
					break;
				case 38:
					PlasmaMap.setLocation(PlasmaMap.getX(), PlasmaMap.getY() + 10);
					RoverMarker.setAngle(90);
					break;
				case 40:
					PlasmaMap.setLocation(PlasmaMap.getX(), PlasmaMap.getY() - 10);
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

	public void setRoverLocation(Point p){
		roverLocation = p;
		PlasmaMap.setLocation((int)((this.getWidth() - PlasmaMap.getWidth()) / 2 - p.getX()), (int)((this.getHeight() - PlasmaMap.getHeight()) / 2 + p.getY()));
	}
	
	public Point getRoverLocation(){
		return roverLocation;
	}
	
	public void setRoverDirection(double direction){
		RoverMarker.setAngle(direction);
	}
	
	public double getRoverDirection(){
		return RoverMarker.getAngle();
	}
	
}
