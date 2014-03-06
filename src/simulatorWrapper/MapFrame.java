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
		contentPane.setComponentZOrder(Grid, contentPane.getComponentCount()-1);
		
		JLabel label = new JLabel();
		label.setBounds(214, 175, 46, 14);
		contentPane.add(label);
	}
}
