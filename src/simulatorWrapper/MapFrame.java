package simulatorWrapper;

import interfaceGUI.InterfaceForm;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import objects.BackgroundImage;

import java.awt.Toolkit;
import javax.swing.JLabel;

public class MapFrame extends JFrame {

	static MapFrame frame;
	private JPanel contentPane;
	private BackgroundImage Grid;
	public MapIcon RoverMarker;

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
		contentPane.setComponentZOrder(Grid, contentPane.getComponentCount()-1);	
	}
	
	private void initalize(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 482, 374);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MapFrame.class.getResource("/Map_with_Compass.png")));
		setTitle("Rover Movement Viewer");
		contentPane.setLayout(null);
		
		Grid = new BackgroundImage();
		Grid.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Position Background.png")));
		Grid.setBounds(0, 0, 482, 374);
		contentPane.add(Grid);
		
		RoverMarker = new MapIcon();
		RoverMarker.setBounds(177, 177, 70, 70);
		RoverMarker.setAngle(90);
		contentPane.add(RoverMarker);
	}
}
