package interfaceGUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import objects.BackgroundImage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

public class InterfaceForm extends JFrame {

	static InterfaceForm frame;
	private JPanel contentPane;
	private Point FrameLocation = new Point(0, 0);
	
	BackgroundImage Background;
	JPanel ProgramBtnsPnl;
		ImageButton LinkBtn;
		ImageButton MailBtn;
		ImageButton MessageBtn;
		ImageButton CommentBtn;
		ImageButton OptionsBtn;
		ImageButton FolderBtn;
		JComboBox PortSelectCombo;
		JLabel MuteIcon;
	JPanel RoverBtnsPnl;
		ImageButton[] RoverBtns = new ImageButton[30];
		JLabel RoverSendLbl;
		JTextField RoverSendTxt;
		JButton RoverSendBtn;
		JLabel RoverDeleteLink;
		JLabel RoverEditLink;
		JLabel RoverAddLink;
	JPanel SatiliteBtnsPnl;
		ImageButton[] SatBtns = new ImageButton[RoverBtns.length];
		JLabel SatSendLbl;
		JTextField SatSendTxt;
		JButton SatSendBtn;
		JLabel SatAddLink;
		JLabel SatEditLink;
		JLabel SatDeleteLink;
	JTextArea SerialDisplayLbl;
	JLabel ConnectionLbl;
	ImageButton MinimizeBtn;
	ImageButton ExitBtn;
	JLabel VersionLbl;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new InterfaceForm();
					InterfaceCode.align();
					//frame.setUndecorated(true);
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					screenSize = new Dimension((int)(screenSize.getWidth() * 0.8), (int)(screenSize.getHeight() * 0.8));
					frame.setSize(screenSize);
					frame.setLocation(0, 0);
				    frame.setVisible(true);
				    frame.setResizable(false);
				    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public InterfaceForm() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent arg0) {
				//frame.setLocation(FrameLocation);
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				InterfaceEvents.windowClosing();
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
				InterfaceEvents.Window_Opened();
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(InterfaceForm.class.getResource("/Dish.png")));
		setTitle("Mock Ground");
		initalize();
	}
	
	private void initalize(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1033, 616);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		ProgramBtnsPnl = new JPanel();
		ProgramBtnsPnl.setOpaque(false);
		ProgramBtnsPnl.setBackground(Color.DARK_GRAY);
		ProgramBtnsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Program Controls", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		ProgramBtnsPnl.setBounds(10, 11, 665, 85);
		contentPane.add(ProgramBtnsPnl);
		ProgramBtnsPnl.setLayout(null);
		
		LinkBtn = new ImageButton();
		LinkBtn.setToolTipText("Refresh Link");
		LinkBtn.setBounds(10, 20, 55, 55);
		LinkBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceEvents.CODE.resetConnection();
			}
		});
		LinkBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Earth_Up.png")));
		ProgramBtnsPnl.add(LinkBtn);
		
		MailBtn = new ImageButton();
		MailBtn.setBounds(140, 20, 55, 55);
		MailBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Mail.png")));
		MailBtn.setToolTipText("View Data");
		MailBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceEvents.CODE.OpenRecievedFiles();
			}
		});
		ProgramBtnsPnl.add(MailBtn);
		
		MessageBtn = new ImageButton();
		MessageBtn.setBounds(400, 20, 55, 55);
		MessageBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Comment_Up.png")));
		MessageBtn.setToolTipText("Send Command");
		MessageBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!MessageBtn.getText().equals("")){
					InterfaceEvents.sendMsg(JOptionPane.showInputDialog(frame, "Enter a Command:", "Write to Serial", JOptionPane.DEFAULT_OPTION));
				}
			}
		});
		ProgramBtnsPnl.add(MessageBtn);
		
		CommentBtn = new ImageButton();
		CommentBtn.setBounds(270, 20, 55, 55);
		CommentBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/New_Post.png")));
		CommentBtn.setToolTipText("Note on Log");
		CommentBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceEvents.addNoteToLog(JOptionPane.showInputDialog(frame, "Note for log:", "Data Log Edit", JOptionPane.DEFAULT_OPTION));
			}
		});
		ProgramBtnsPnl.add(CommentBtn);
		
		OptionsBtn = new ImageButton();
		OptionsBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Gear.png")));
		OptionsBtn.setToolTipText("View Activity Log");
		OptionsBtn.setBounds(335, 20, 55, 55);
		OptionsBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// Show Options Pnl
			}
		});
		ProgramBtnsPnl.add(OptionsBtn);
		
		FolderBtn = new ImageButton();
		FolderBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Folder.png")));
		FolderBtn.setToolTipText("View Files");
		FolderBtn.setBounds(205, 20, 55, 55);
		FolderBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().open(new File("").getAbsoluteFile());
				} catch (IOException e) {
					InterfaceEvents.CODE.HandleError(e);
				}
			}
		});
		ProgramBtnsPnl.add(FolderBtn);
		
		PortSelectCombo = new JComboBox();
		PortSelectCombo.setMaximumRowCount(20);
		PortSelectCombo.setFont(new Font("OCR B MT", Font.BOLD, 13));
		PortSelectCombo.setModel(new DefaultComboBoxModel(new String[] {"COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM10", "COM11", "COM12", "COM13", "COM14", "COM15", "COM16", "COM17", "COM18", "COM19", "COM20"}));
		PortSelectCombo.setSelectedIndex(12);
		PortSelectCombo.setBounds(570, 20, 85, 20);
		PortSelectCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceEvents.COMPortChanged();
			}
		});
		ProgramBtnsPnl.add(PortSelectCombo);
		
		JLabel lblSelectedPort = new JLabel("Selected Port:");
		lblSelectedPort.setForeground(Color.WHITE);
		lblSelectedPort.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		lblSelectedPort.setBounds(477, 22, 83, 14);
		ProgramBtnsPnl.add(lblSelectedPort);
		
		ConnectionLbl = new JLabel("Connected for 0 min.");
		ConnectionLbl.setBounds(497, 53, 158, 25);
		ProgramBtnsPnl.add(ConnectionLbl);
		ConnectionLbl.setForeground(Color.WHITE);
		ConnectionLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		ConnectionLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		
		ImageButton PingBtn = new ImageButton();
		PingBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceEvents.CODE.pingRover();
			}
		});
		PingBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Wi-Fi.png")));
		PingBtn.setToolTipText("Ping Rover");
		PingBtn.setBounds(75, 20, 55, 55);
		ProgramBtnsPnl.add(PingBtn);
		
		MuteIcon = new JLabel(new ImageIcon(InterfaceForm.class.getResource("/Mute.png")));
		MuteIcon.setToolTipText("Cannot Send Messages");
		MuteIcon.setVisible(false);
		MuteIcon.setBounds(477, 51, 24, 24);
		ProgramBtnsPnl.add(MuteIcon);
		
		SerialDisplayLbl = new JTextArea();
		SerialDisplayLbl.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		SerialDisplayLbl.setBounds(685, 85, 322, 299);
		contentPane.add(SerialDisplayLbl);
		SerialDisplayLbl.setLineWrap(true);
		SerialDisplayLbl.setFont(new Font("Miriam Fixed", Font.PLAIN, 14));
		SerialDisplayLbl.setEditable(false);
		SerialDisplayLbl.setWrapStyleWord(true);
		
		RoverBtnsPnl = new JPanel();
		RoverBtnsPnl.setOpaque(false);
		RoverBtnsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Rover Controls", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		RoverBtnsPnl.setBounds(10, 107, 665, 106);
		contentPane.add(RoverBtnsPnl);
		RoverBtnsPnl.setLayout(null);
		
		RoverSendLbl = new JLabel("Send Command:");
		RoverSendLbl.setForeground(Color.WHITE);
		RoverSendLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverSendLbl.setBounds(10, 81, 93, 14);
		RoverBtnsPnl.add(RoverSendLbl);
		
		RoverSendTxt = new JTextField();
		RoverSendTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 10){
					InterfaceEvents.sendRoverMsg();
				}
			}
		});
		RoverSendTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverSendTxt.setBounds(113, 77, 200, 23);
		RoverBtnsPnl.add(RoverSendTxt);
		RoverSendTxt.setColumns(10);
		
		RoverSendBtn = new JButton("Send");
		RoverSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceEvents.sendRoverMsg();
			}
		});
		RoverSendBtn.setOpaque(false);
		RoverSendBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		RoverSendBtn.setBounds(314, 78, 86, 23);
		RoverBtnsPnl.add(RoverSendBtn);
		
		RoverDeleteLink = new JLabel("<HTML><U>Delete</U></HTML>");
		RoverDeleteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceEvents.roverLinkClicked(2);
			}
		});
		RoverDeleteLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverDeleteLink.setForeground(Color.LIGHT_GRAY);
		RoverDeleteLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverDeleteLink.setBounds(612, 81, 43, 14);
		RoverBtnsPnl.add(RoverDeleteLink);
		
		RoverEditLink = new JLabel("<HTML><U>Edit</U></HTML>");
		RoverEditLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceEvents.roverLinkClicked(1);
			}
		});
		RoverEditLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverEditLink.setForeground(Color.LIGHT_GRAY);
		RoverEditLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverEditLink.setBounds(580, 81, 22, 14);
		RoverBtnsPnl.add(RoverEditLink);
		
		RoverAddLink = new JLabel("<HTML><U>Add</U></HTML>");
		RoverAddLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceEvents.roverLinkClicked(0);
			}
		});
		RoverAddLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		RoverAddLink.setForeground(Color.LIGHT_GRAY);
		RoverAddLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		RoverAddLink.setBounds(548, 81, 22, 14);
		RoverBtnsPnl.add(RoverAddLink);
		
		int x = 0;
		while (x < RoverBtns.length){
			RoverBtns[x] = new ImageButton();
			RoverBtns[x].setBounds(10 + 65 * x, 20, 55, 55);
			RoverBtns[x].setEnabled(false);
			RoverBtns[x].setHorizontalTextPosition(SwingConstants.CENTER);
			RoverBtns[x].setIcon(null);
			final int hold = x;
			RoverBtns[x].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if (RoverBtns[hold].isEnabled()){
						InterfaceEvents.actionButtonClicked(0, hold);
					}
				}
			});
			RoverBtnsPnl.add(RoverBtns[x]);
			x++;
		}
		
		SatiliteBtnsPnl = new JPanel();
		SatiliteBtnsPnl.setOpaque(false);
		SatiliteBtnsPnl.setBounds(10, 224, 665, 123);
		SatiliteBtnsPnl.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(192, 192, 192)), "Satilite Controls", TitledBorder.LEADING, TitledBorder.TOP, new Font("Iskoola Pota", Font.BOLD, 16), Color.WHITE));
		SatiliteBtnsPnl.setLayout(null);
		contentPane.add(SatiliteBtnsPnl);
		
		SatSendLbl = new JLabel("Send Command:");
		SatSendLbl.setForeground(Color.WHITE);
		SatSendLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatSendLbl.setBounds(10, 92, 93, 14);
		SatiliteBtnsPnl.add(SatSendLbl);
		
		SatSendTxt = new JTextField();
		SatSendTxt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10){
					InterfaceEvents.sendSatMessage();
				}
			}
		});
		SatSendTxt.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatSendTxt.setColumns(10);
		SatSendTxt.setBounds(113, 88, 200, 23);
		SatiliteBtnsPnl.add(SatSendTxt);
		
		SatSendBtn = new JButton("Send");
		SatSendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InterfaceEvents.sendSatMessage();
			}
		});
		SatSendBtn.setOpaque(false);
		SatSendBtn.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, 12));
		SatSendBtn.setBounds(314, 89, 86, 23);
		SatiliteBtnsPnl.add(SatSendBtn);
		
		SatAddLink = new JLabel("<HTML><U>Add</U></HTML>");
		SatAddLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceEvents.satLinkClicked(0);
			}
		});
		SatAddLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SatAddLink.setForeground(Color.LIGHT_GRAY);
		SatAddLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatAddLink.setBounds(548, 97, 22, 14);
		SatiliteBtnsPnl.add(SatAddLink);
		
		SatEditLink = new JLabel("<HTML><U>Edit</U></HTML>");
		SatEditLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceEvents.satLinkClicked(1);
			}
		});
		SatEditLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SatEditLink.setForeground(Color.LIGHT_GRAY);
		SatEditLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatEditLink.setBounds(580, 97, 22, 14);
		SatiliteBtnsPnl.add(SatEditLink);
		
		SatDeleteLink = new JLabel("<HTML><U>Delete</U></HTML>");
		SatDeleteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InterfaceEvents.satLinkClicked(2);
			}
		});
		SatDeleteLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		SatDeleteLink.setForeground(Color.LIGHT_GRAY);
		SatDeleteLink.setFont(new Font("Iskoola Pota", Font.PLAIN, 14));
		SatDeleteLink.setBounds(612, 97, 43, 14);
		SatiliteBtnsPnl.add(SatDeleteLink);
		
		x = 0;
		while (x < SatBtns.length){
			SatBtns[x] = new ImageButton();
			SatBtns[x].setBounds(10 + 65 * x, 20, 55, 55);
			SatBtns[x].setEnabled(false);
			SatBtns[x].setHorizontalTextPosition(SwingConstants.CENTER);
			SatBtns[x].setIcon(null);
			final int hold = x;
			SatBtns[x].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if (SatBtns[hold].isEnabled()){
						InterfaceEvents.actionButtonClicked(1, hold);
					}
				}
			});
			SatiliteBtnsPnl.add(SatBtns[x]);
			x++;
		}
		
		ExitBtn = new ImageButton();
		ExitBtn.setOpaque(false);
		ExitBtn.setBorder(null);
		ExitBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		ExitBtn.setImage(new ImageIcon(InterfaceForm.class.getResource("/Close.png")));
		ExitBtn.setHoverImage(new ImageIcon(InterfaceForm.class.getResource("/Close Hover.png")));
		ExitBtn.setToolTipText("");
		ExitBtn.setMargin(0);
		ExitBtn.setBounds(959, 11, 48, 19);
		ExitBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				InterfaceEvents.CODE.closeProgram();
				System.exit(0);
			}
		});
		contentPane.add(ExitBtn);
		
		MinimizeBtn = new ImageButton();
		MinimizeBtn.setOpaque(false);
		MinimizeBtn.setToolTipText("");
		MinimizeBtn.setBorder(null);
		MinimizeBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		MinimizeBtn.setImage(new ImageIcon(InterfaceForm.class.getResource("/Minimize.png")));
		MinimizeBtn.setHoverImage(new ImageIcon(InterfaceForm.class.getResource("/Minimize Hover.png")));
		MinimizeBtn.setMargin(0);
		MinimizeBtn.setBounds(931, 11, 29, 19);
		MinimizeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frame.setState(Frame.ICONIFIED);
			}
		});
		contentPane.add(MinimizeBtn);
		
		VersionLbl = new JLabel("Health Prognostics Rover Project    CSM    v 2.1");
		VersionLbl.setHorizontalAlignment(SwingConstants.CENTER);
		VersionLbl.setFont(new Font("Iskoola Pota", Font.PLAIN, 12));
		VersionLbl.setForeground(Color.LIGHT_GRAY);
		VersionLbl.setBounds(354, 553, 251, 14);
		contentPane.add(VersionLbl);
		
		Background = new BackgroundImage();
		Background.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Background.png")));
		Background.setBounds(0, 0, 1007, 567);
		contentPane.add(Background);
		
	}
}
