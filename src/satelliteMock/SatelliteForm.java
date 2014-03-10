package satelliteMock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class SatelliteForm extends JFrame {

	static SatelliteForm frame;
	private JPanel contentPane;
	JTextField FileLocationTxt;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new SatelliteForm();
					SatelliteCode.align();
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setLocation((int)(screenSize.getWidth() - frame.getWidth() - 20), (int)(screenSize.getHeight() - frame.getHeight() - 20 - 50));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SatelliteForm() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				SatelliteEvents.Window_Opened();
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(SatelliteForm.class.getResource("/Satelite.png")));
		setTitle("Mock Satellite");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SatelliteEvents.BrowseForFile_Clicked();
			}
		});
		panel.add(btnBrowse, BorderLayout.EAST);
		
		FileLocationTxt = new JTextField();
		FileLocationTxt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				SatelliteEvents.CODE.newFileConnected();
			}
		});
		FileLocationTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10){
					SatelliteEvents.CODE.newFileConnected();
				}
			}
		});
		FileLocationTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 15));
		panel.add(FileLocationTxt, BorderLayout.CENTER);
		FileLocationTxt.setColumns(10);
		
		JLabel txtChooseinoFile = new JLabel();
		txtChooseinoFile.setFont(new Font("Iskoola Pota", Font.PLAIN, 13));
		txtChooseinoFile.setBorder(null);
		txtChooseinoFile.setOpaque(false);
		txtChooseinoFile.setText("Choose .ino File:");
		panel.add(txtChooseinoFile, BorderLayout.NORTH);
	}

}
