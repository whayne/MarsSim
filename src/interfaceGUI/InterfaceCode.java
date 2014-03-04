package interfaceGUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import objects.Globals;
import objects.ThreadTimer;
import objects.ZDate;

public class InterfaceCode {

	static InterfaceForm GUI = new InterfaceForm();
	OutputStream outputStream;
    InputStream inputStream;
    boolean outputBufferEmptyFlag = false;
    ThreadTimer serialCheck;	
    
    File dataFile;
    FileWriter LogFile;
	String connectedPort = "COM13";
	ZDate DateTime;
	ThreadTimer clock;
	
	int connectionTime = 0;
	int countSec = 0;
	boolean Connected = false;
	boolean muted = false;
	Runnable confirmMessage;
	Runnable failMessage;
	
	boolean editingRover = false;
	boolean deletingRover = false;
	boolean editingSat = false;
	boolean deletingSat = false;
	boolean receivingPhoto = false;
	
	boolean listening = false;
	String listenFor;
	ThreadTimer listenTimer;
	Runnable listenAction;
	Runnable listenFail;
	String[] receivedFiles = new String[0];
	
	int selectedEditBtn = 0;
	ImageIcon selectedEditIcon = null;
	Color baseColor = new Color(240, 240, 240);
	String[][] actionCommands = new String[2][30];
	String[][] actionTips = new String[2][30];
	String[][] actionIcons = new String[2][30];
	
	
	// SETUP
	
	static void align(){
		GUI = InterfaceForm.frame;
	}
	
	public void initalize(){
		alignComponents();
		serialCheck = new ThreadTimer((int)(400/Globals.getTimeScale()), new Runnable(){
			public void run(){
				InterfaceEvents.CODE.updateSerialCom();
			}
		}, ThreadTimer.FOREVER);
		DateTime = new ZDate();
		DateTime.setFormat("\t\t[hh:mm:ss]");
		clock = new ThreadTimer((int)(1000/Globals.getTimeScale()), new Runnable(){
			public void run(){
				if (Connected){
					DateTime.advanceClock();
					countSec++;
					if (countSec == 60){
						connectionTime++;
						countSec = 0;
						GUI.ConnectionLbl.setText("Connected for " + connectionTime + " min.");
					}
				}
				else {
					GUI.ConnectionLbl.setText("Not Connected");
				}
			}
		}, ThreadTimer.FOREVER);
		try {
			/* dataFile = new File("Rover Logs\\Log File " + DateTime.toString("MMddhhmm") + ".txt");
			dataFile.createNewFile();
			LogFile = new FileWriter(dataFile);
			LogFile.write("Connection Opened with " + connectedPort + DateTime.toString(" on MM-dd-yyyy at hh:mm:ss"));
			LogFile.write(System.getProperty("line.separator"));
			LogFile.write(System.getProperty("line.separator")); */
		} catch (Exception e) {
			HandleError(e);
		}
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream("CommandString.dll");
			in = new ObjectInputStream(fis);
			SaveFile input = (SaveFile) in.readObject();
			actionCommands = input.getCommands();
			actionTips = input.getTooltips();
			actionIcons = input.getIcons();
			UpdateActionBtns();
			in.close();
		} 
		catch (FileNotFoundException e){
			int x = 0;
			while (x < actionCommands[0].length){
				actionCommands[0][x] = "";
				actionCommands[1][x] = "";
				actionTips[0][x] = "";
				actionTips[1][x] = "";
				actionIcons[0][x] = "";
				actionIcons[1][x] = "";
				x++;
			}
		}
		catch (IOException ex) {
			HandleError(ex);
		} 
		catch (ClassNotFoundException ex) {
			HandleError(ex);
		}
		confirmMessage = new Runnable(){
			public void run(){
				(new PopUp()).showConfirmDialog("Message was successfully sent.", "Message Confirmed", PopUp.DEFAULT_OPTIONS);
			}
		};
		failMessage = new Runnable(){
			public void run(){
				(new PopUp()).showConfirmDialog("No message confirmation was recieved.", "Message Failed", PopUp.DEFAULT_OPTIONS);
			}
		};
	}
	
	private void alignComponents(){
		GUI.Background.setBounds(0, 0, GUI.getWidth(), GUI.getHeight());
		GUI.ExitBtn.setLocation(GUI.getWidth() - GUI.ExitBtn.getWidth() - 10, -1);
		GUI.MinimizeBtn.setLocation((int) (GUI.ExitBtn.getLocation().getX() - GUI.MinimizeBtn.getWidth() + 1), -1);
		GUI.SerialDisplayLbl.setSize(GUI.getWidth() / 4, GUI.getHeight() - 20 - GUI.ExitBtn.getBottom());
		GUI.SerialDisplayLbl.setLocation(GUI.getWidth() - 10 - GUI.SerialDisplayLbl.getWidth(), GUI.ExitBtn.getBottom() + 10);
		GUI.VersionLbl.setLocation((GUI.getWidth() - GUI.VersionLbl.getWidth()) / 2, GUI.getHeight() - GUI.VersionLbl.getHeight() - 3);
		
		GUI.RoverBtnsPnl.setBounds(10, GUI.ProgramBtnsPnl.getHeight() + 20, (int) (GUI.SerialDisplayLbl.getLocation().getX() - 20), 160 + GUI.RoverSendTxt.getHeight());
		GUI.RoverSendLbl.setLocation(10, GUI.RoverBtnsPnl.getHeight() - 10 - GUI.RoverSendTxt.getHeight() + 4);
		GUI.RoverSendTxt.setLocation(10 + GUI.RoverSendLbl.getWidth() + 5, (int) (GUI.RoverSendLbl.getLocation().getY() - 4));
		GUI.RoverSendBtn.setLocation((int) (GUI.RoverSendTxt.getLocation().getX() + GUI.RoverSendTxt.getWidth() + 3), (int) GUI.RoverSendTxt.getLocation().getY());
		GUI.RoverDeleteLink.setLocation(GUI.RoverBtnsPnl.getWidth() - 10 - GUI.RoverDeleteLink.getWidth(), (int) GUI.RoverSendLbl.getLocation().getY());
		GUI.RoverEditLink.setLocation((int) (GUI.RoverDeleteLink.getLocation().getX() - 10 - GUI.RoverEditLink.getWidth()), (int) GUI.RoverSendLbl.getLocation().getY());
		GUI.RoverAddLink.setLocation((int) (GUI.RoverEditLink.getLocation().getX() - 10 - GUI.RoverAddLink.getWidth()), (int) GUI.RoverSendLbl.getLocation().getY());
		
		GUI.SatiliteBtnsPnl.setBounds(10, 10 + GUI.ProgramBtnsPnl.getHeight() + 10 + GUI.RoverBtnsPnl.getHeight() + 10, GUI.RoverBtnsPnl.getWidth(), 160 + GUI.SatSendTxt.getHeight());
		GUI.SatSendLbl.setLocation(10, GUI.SatiliteBtnsPnl.getHeight() - 10 - GUI.SatSendTxt.getHeight() + 4);
		GUI.SatSendTxt.setLocation(10 + GUI.SatSendLbl.getWidth() + 5, (int) (GUI.SatSendLbl.getLocation().getY() - 4));
		GUI.SatSendBtn.setLocation((int) (GUI.SatSendTxt.getLocation().getX() + GUI.SatSendTxt.getWidth() + 3), (int) GUI.SatSendTxt.getLocation().getY());
		GUI.SatDeleteLink.setLocation(GUI.SatiliteBtnsPnl.getWidth() - 10 - GUI.SatDeleteLink.getWidth(), (int) GUI.SatSendLbl.getLocation().getY());
		GUI.SatEditLink.setLocation((int) (GUI.SatDeleteLink.getLocation().getX() - 10 - GUI.SatEditLink.getWidth()), (int) GUI.SatSendLbl.getLocation().getY());
		GUI.SatAddLink.setLocation((int) (GUI.SatEditLink.getLocation().getX() - 10 - GUI.SatAddLink.getWidth()), (int) GUI.SatSendLbl.getLocation().getY());
		
		int spacing = (GUI.RoverBtnsPnl.getWidth() - 20 - 55 * (GUI.SatBtns.length / 2)) / (GUI.SatBtns.length / 2 - 1);
		int x = 0;
		while (x < GUI.RoverBtns.length){
			GUI.RoverBtns[x].setLocation(10 + (55 + spacing) * ((x + (GUI.RoverBtns.length / 2)) % (GUI.RoverBtns.length / 2)), 20 + 65 * (x / (GUI.RoverBtns.length / 2)));
			GUI.SatBtns[x].setLocation(10 + (55 + spacing) * ((x + (GUI.RoverBtns.length / 2)) % (GUI.RoverBtns.length / 2)), 20 + 65 * (x / (GUI.RoverBtns.length / 2)));
			x++;
		}
	}
	
	public void closeProgram(){
		/* try {
			LogFile.write(System.getProperty("line.separator"));
			writeToLog("Communication Closed");
			LogFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		} */
	}
	
	public void pingRover(){
		GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Pinging Rover...\n");
		writeToSerial("s g ^", true);
		listenForSignal("g ^", new Runnable(){
			public void run(){
				Connected = true;
				GUI.ConnectionLbl.setText("Connected for 0 min.");
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Rover Connected: " + DateTime.toString("hh:mm:ss") + "\n");
				(new PopUp()).showConfirmDialog("Rover connected.", "Ping Confirm", PopUp.DEFAULT_OPTIONS);
			}
		}, new Runnable(){
			public void run(){
				Connected = false;
				GUI.ConnectionLbl.setText("Not Connected.");
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Rover did not respond.\n");
				(new PopUp()).showConfirmDialog("No Rover found.", "Ping Failed", PopUp.DEFAULT_OPTIONS);
			}
		}, 
		10);
	}
	
		
	// COM CONNECTION STUFF
	
	public void resetConnection(){
		if (Connected){
			pingRover();
		}
	}
	
	public void changeCOMPort(){
		connectionTime = 0;
		connectedPort = (String)GUI.PortSelectCombo.getSelectedItem();
		GUI.ConnectionLbl.setText("Connected for " + connectionTime + " min.");
		// writeToLog("Connection Changed to " + connectedPort);
		resetConnection();
	}

	public void writeToSerial(String msg){
		if (Connected && !muted){
			//writeToLog("Command sent: \'" + msg + "\'" + DateTime.toString());
			//try {
            // 	outputStream.write(msg.getBytes());
        	//} catch (Exception e) {
        	//	GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Message Failed" + "\n");
        	//	pingRover();
        	//}
			char[] output = msg.toCharArray();
			int x = 0;
			while (x < output.length){
				Globals.writeToSerial(output[x], 'g'); // Write to Serial one char at a time
				try {
					Thread.sleep((int)(20 / Globals.getTimeScale())); // Pause for sending
				} catch (InterruptedException e) {}
				x++;
			}
		}
	}
	
	public void writeToSerial(String msg, boolean override){
		if ((Connected || override) && !muted){
			//writeToLog("Command sent: \'" + msg + "\'" + DateTime.toString());
			//try {
            // 	outputStream.write(msg.getBytes());
        	//} catch (Exception e) {
        	//	GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Message Failed" + "\n");
        	//	pingRover();
        	//}
			char[] output = msg.toCharArray();
			int x = 0;
			while (x < output.length){
				Globals.writeToSerial(output[x], 'g'); // Write to Serial one char at a time
				try {
					Thread.sleep((int)(20 / Globals.getTimeScale())); // Pause for sending
				} catch (InterruptedException e) {}
				x++;
			}
		}
	}
	
	public void updateSerialCom(){
		if (!receivingPhoto){
			char[] input = readFromSerial().toCharArray();
			if (input.length > 2){
				if (input[0] == 'g'){
					if (input[2] == 'n'){
						String data = "";
						int x = 4;
						while (x < input.length){
							data += input[x];
							x++;
						}
						GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Recieved: " + data + "\n");
						// writeToLog("Recieved Note: " + data);
					}
					else if (input[2] == 'i'){
						receivingPhoto = true;
						int filelength = Integer.parseInt(buildString(input, 4, input.length - 1));
						if (filelength < 0){
							filelength += 65536;
						}
						ReadPhoto(filelength);
					}
					else if (input[2] == '}'){
						muted = true;
						GUI.MuteIcon.setVisible(true);
					}
					else if (input[2] == '{'){
						muted = false;
						GUI.MuteIcon.setVisible(false);
					}
				}
				else {
					if (input.equals("Data Could Not be Parsed\n".toCharArray())){
						GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Data Could Not be Parsed\n");
					}
				}
			}
		}
	}
	
	private String readFromSerial(){
		if (!connectedPort.equals("")){
			try {
					//if (inputStream.available() > 0){
				if (Globals.RFAvailable('g') > 0){
					// System.out.println("Available");
					Thread.sleep((int)(20 / Globals.getTimeScale()));
					String out = "";
						//while(inputStream.available() > 0) {
					while (Globals.RFAvailable('g') > 0) {
							//out += (char)(inputStream.read());
						out += (char)Globals.ReadSerial('g');
					}
					if (listening){
						if (out.equals(listenFor)){
							new ThreadTimer(0, listenAction, 1);
						}
					}
					return out;
				}
				else {
					return "";
				}
			}	
			catch(Exception exe) {
				if (connectedPort.equals("")){
					return "";
				}
				else {
					return "Data Could Not be Parsed\n";
				}
			}
		}
		else {
			return "";
		}
	}
	
	private void listenForSignal(String msg, final Runnable passaction, final Runnable failaction, int secs){
		listening = true;
		listenFor = msg;
		listenAction = new Runnable(){
			public void run(){
				listenTimer.interrupt();
				listening = false;
				passaction.run();
			}
		};
		listenFail = new Runnable(){
			public void run(){
				listening = false;
				failaction.run();
			}
		};
		listenTimer = new ThreadTimer((int)(secs*1000/Globals.getTimeScale()), listenFail, 1);
	}
	
	
	// ACTION BUTTONS
	
	public void ActionButtonClicked(int section, int which){
		if (editingRover){
			if (section == 0){
				editRover2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else if (deletingRover){
			if (section == 0){
				deleteRover2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else if (editingSat){
			if (section == 1){
				editSat2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else if (deletingSat){
			if (section == 1){
				deleteSat2(which);
			}
			else {
				cancelProgrammer();
			}
		}
		else {
			if (!actionCommands[section][which].equals("")){
	            GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Sent: \"" + actionCommands[section][which] + "\"\n");
				if (section == 0){
					writeToSerial("s g " + actionCommands[section][which]);
					listenForSignal("g #", new Runnable(){
						public void run(){
							listenForSignal("g %", new Runnable(){
								public void run(){
									new ThreadTimer(0, confirmMessage, 1);
								}
							}, new Runnable(){
								public void run(){
									new ThreadTimer(0, failMessage, 1);
								}
							}, 5);
						}
					}, new Runnable(){
						public void run(){
							new ThreadTimer(0, failMessage, 1);
						}
					}, 4);
				}
				else {
					writeToSerial("s c " + actionCommands[section][which]);
					listenForSignal("g #", new Runnable(){
						public void run(){
							new ThreadTimer(0, confirmMessage, 1);
						}
					}, new Runnable(){
						public void run(){
							new ThreadTimer(0, failMessage, 1);
						}
					}, 4);
				}
			}
		}
	}
	
	public void sendRoverCommand(){
		if (!GUI.RoverSendTxt.getText().equals("")){
			if (!Connected){
				GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "\nYou are not Connected.");
			}
			writeToSerial("s g " + GUI.RoverSendTxt.getText());
            GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Sent: \"" + GUI.RoverSendTxt.getText() + "\"\n");
            listenForSignal("g #", new Runnable(){
				public void run(){
					listenForSignal("g %", new Runnable(){
						public void run(){
							new ThreadTimer(0, confirmMessage, 1);
						}
					}, new Runnable(){
						public void run(){
							new ThreadTimer(0, failMessage, 1);
						}
					}, 5);
				}
			}, new Runnable(){
				public void run(){
					new ThreadTimer(0, failMessage, 1);
				}
			}, 4);
			GUI.RoverSendTxt.setText("");
		}
		else {
			(new PopUp()).showConfirmDialog("You must enter a message into the field.", "Message Failed", PopUp.DEFAULT_OPTIONS);
		}
	}
	
	public void sendSatCommand(){
		if (!GUI.SatSendTxt.getText().equals("")){
			writeToSerial("s c " + GUI.SatSendTxt.getText());
            GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Sent: \"" + GUI.SatSendTxt.getText() + "\"\n");
			listenForSignal("g #", new Runnable(){
				public void run(){
					new ThreadTimer(0, confirmMessage, 1);
				}
			}, new Runnable(){
				public void run(){
					new ThreadTimer(0, failMessage, 1);
				}
			}, 4);
			GUI.SatSendTxt.setText("");
		}
		else {
			(new PopUp()).showConfirmDialog("You must enter a message into the field.", "Message Failed", PopUp.DEFAULT_OPTIONS);
		}
	}
	
	private void UpdateActionBtns(){
		int x = 0;
		while (x < GUI.RoverBtns.length){
			if (!actionCommands[0][x].equals("")){
				GUI.RoverBtns[x].setToolTipText(actionTips[0][x]);
			}
			else {
				GUI.RoverBtns[x].setToolTipText("Unassigned");
			}
			if (!actionCommands[1][x].equals("")){
				GUI.SatBtns[x].setToolTipText(actionTips[1][x]);
			}
			else {
				GUI.SatBtns[x].setToolTipText("Unassigned");
			}
			try {
				GUI.RoverBtns[x].setImage(new ImageIcon(InterfaceForm.class.getResource("/" + actionIcons[0][x])));
			}
			catch (Exception e) {
				GUI.RoverBtns[x].setImage(null);
			}
			GUI.RoverBtns[x].setEnabled(!actionCommands[0][x].equals(""));
			try {
				GUI.SatBtns[x].setImage(new ImageIcon(InterfaceForm.class.getResource("/" + actionIcons[1][x])));
			}
			catch (Exception e){
				GUI.SatBtns[x].setImage(null);
			}
			GUI.SatBtns[x].setEnabled(!actionCommands[1][x].equals(""));
			x++;
		}
	}
	
	
	// ACTION BUTTON EDITING
	
	public void addRoverBtn(){
		new ThreadTimer(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", "", "Tool Tip", "", "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, "", "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							int x = 0;
							while (x < GUI.RoverBtns.length){
								if (actionCommands[0][x].equals("")){
									actionCommands[0][x] = data[1];
									actionTips[0][x] = data[2];
									actionIcons[0][x] = data[3];
									break;
								}
								x++;
							}
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
			}
		}, 1);
	}
	
	public void editRoverBtn1(){
		editingRover = true;
		deletingRover = false;
		editingSat = false;
		deletingSat = false;
		GUI.RoverDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
		int x = 0;
		while (x < GUI.RoverBtns.length){
			GUI.RoverBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			x++;
		}
	}
	
	public void deleteRoverBtn1(){
		if (editingRover || deletingRover){
			cancelProgrammer();
		}
		else {
			editingRover = false;
			deletingRover = true;
			editingSat = false;
			deletingSat = false;
			GUI.RoverDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
			int x = 0;
			while (x < GUI.RoverBtns.length){
				GUI.RoverBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				x++;
			}
		}
	}
	
	private void editRover2(final int which){
		new ThreadTimer(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", actionCommands[0][which], "Tool Tip", actionTips[0][which], "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, actionIcons[0][which], "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							actionCommands[0][which] = data[1];
							actionTips[0][which] = data[2];
							actionIcons[0][which] = data[3];
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
				cancelProgrammer();
			}
		}, 1);
	}
	
	private void deleteRover2(int which){
		int x = which;
		while (x < GUI.RoverBtns.length - 1){
			actionCommands[0][x] = actionCommands[0][x + 1];
			actionTips[0][x] = actionTips[0][x + 1];
			actionIcons[0][x] = actionIcons[0][x + 1];
			x++;
		}
		actionCommands[0][x] = "";
		actionTips[0][x] = "";
		actionIcons[0][x] = "";
		UpdateActionBtns();
		SaveProgrammer();
		cancelProgrammer();
	}
	
	public void addSatBtn(){
		new ThreadTimer(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", "", "Tool Tip", "", "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, "", "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							int x = 0;
							while (x < GUI.RoverBtns.length){
								if (actionCommands[1][x].equals("")){
									actionCommands[1][x] = data[1];
									actionTips[1][x] = data[2];
									actionIcons[1][x] = data[3];
									break;
								}
								x++;
							}
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
			}
		}, 1);
	}
	
	public void editSatBtn1(){
		editingRover = false;
		deletingRover = false;
		editingSat = true;
		deletingSat = false;
		GUI.SatDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
		int x = 0;
		while (x < GUI.SatBtns.length){
			GUI.SatBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			x++;
		}
	}
	
	public void deleteSatBtn1(){
		if (editingSat || deletingSat){
			cancelProgrammer();
		}
		else {
			editingRover = false;
			deletingRover = false;
			editingSat = false;
			deletingSat = true;
			GUI.SatDeleteLink.setText("<HTML><U>Cancel</U></HTML>");
			int x = 0;
			while (x < GUI.SatBtns.length){
				GUI.SatBtns[x].setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				x++;
			}
		}		
	}
	
	private void editSat2(final int which){
		new ThreadTimer(0, new Runnable(){
			public void run(){
				String[] data;
				boolean go = true;
				while (go){		
					data = (new PopUp()).showPromptDialog("Button Command", actionCommands[1][which], "Tool Tip", actionTips[1][which], "Icon", new String[] { "", "About_Page.png", "Add.png", "Anchor.png", "Bacteria.png", "Bacteria_2.png", "Band_Aide.png", "Battery.png", "Bottom_Left.png", "Bottom_Left_Shaded.png", "Bottom_Right.png", "Bottom_Right_Shaded.png", "Caduceus.png", "Calandar.png", "Calculator.png", "Camer_2.png", "Camera.png", "Cancel.png", "Cancel_2.png", "Chain.png", "Circle_CCW.png", "Circle_CCW_Shaded.png", "Circle_CW.png", "Circle_CW_Shaded.png", "Comment.png", "Comment_Up.png", "Cone.png", "Controler.png", "Controller_2.png", "Dish.png", "Double_Arrow_CCW.png", "Double_Arrow_CW.png", "Down.png", "Down_Left.png", "Down_Left_Shaded.png", "Down_Right.png", "Down_Right_Shaded.png", "Down_Shaded.png", "Earth.png", "Earth_Up.png", "Expand.png", "Expand_Shaded.png", "Eye.png", "File_AVI.png", "File_DAT.png", "File_DOC.png", "File_GIF.png", "File_HTML.png", "File_JPG.png", "File_MP4.png", "File_PDF.png", "File_PNG.png", "File_PPT.png", "File_TXT.png", "File_XLS.png", "File_ZIP.png", "Finder.png", "Fire.png", "Flashlight.png", "Folder.png", "Folder_Up.png", "Gear.png", "GPS.png", "GPS_Pin.png", "Green_Check.png", "Handicap.png", "Handicap_Shaded.png", "Hourglass.png", "Key.png", "Lamp.png", "Left.png", "Left_Down.png", "Left_Down_Shaded.png", "Left_Shaded.png", "Left_Up.png", "Left_Up_Shaded.png", "Lifesaver.png", "Linked_Arrows_CW.png", "Mail.png", "Mail_Message.png", "Map_with_Compass.png", "Map_with_Pins.png", "Mic.png", "Mic_2.png", "New_Page.png", "New_Post.png", "Pencil.png", "Pie_Chart.png", "Printer.png", "Processor.png", "Push_Pin.png", "Push_Pin_1.png", "Push_Pin_2.png", "Red_X.png", "Right.png", "Right_Down.png", "Right_Down_Shaded.png", "Right_Shaded.png", "Right_Up.png", "Right_Up_Shaded.png", "Rocket.png", "Rover.png", "Save.png", "Scanner.png", "Send_Mail.png", "Server.png", "Server_2.png", "Shield.png", "Snowflake.png", "Spyglass.png", "Steering_Wheel.png", "Sthetoscope.png", "Stop.png", "Stop_Shaded.png", "Switch.png", "Tanget_Line.png", "Telescope.png", "Temp.png", "Temp_Cold.png", "Temp_Hot.png", "Terminal.png", "Tire.png", "Tools.png", "Top_Left.png", "Top_Left_Shaded.png", "Top_Right.png", "Top_Right_Shaded.png", "Tornado.png", "Up.png", "Up_Left.png", "Up_Left_Shaded.png", "Up_Right.png", "Up_Right_Shaded.png", "Up_Shaded.png", "USB.png", "USB_2.png", "Wall.png", "Wand.png", "Wi-Fi.png", "World_Link.png", "XRay.png" }, actionIcons[1][which], "Create New Button");
					go = Integer.parseInt(data[0]) == PopUp.OK_OPTION;
					if (go){
						if (!data[1].equals("") && !data[3].equals("")){
							actionCommands[1][which] = data[1];
							actionTips[1][which] = data[2];
							actionIcons[1][which] = data[3];
							break;
						}
						else {
							(new PopUp()).showConfirmDialog("Required data was left unfilled.", "Process Failed", PopUp.DEFAULT_OPTIONS);
						}
					}
				}
				UpdateActionBtns();
				SaveProgrammer();
				cancelProgrammer();
			}
		}, 1);
	}
	
	private void deleteSat2(int which){
		int x = which;
		while (x < GUI.SatBtns.length - 1){
			actionCommands[1][x] = actionCommands[1][x + 1];
			actionTips[1][x] = actionTips[1][x + 1];
			actionIcons[1][x] = actionIcons[1][x + 1];
			x++;
		}
		actionCommands[1][x] = "";
		actionTips[1][x] = "";
		actionIcons[1][x] = "";
		UpdateActionBtns();
		SaveProgrammer();
		cancelProgrammer();
	}
	
	public void cancelProgrammer(){
		editingRover = false;
		deletingRover = false;
		editingSat = false;
		deletingSat = false;
		GUI.RoverDeleteLink.setText("<HTML><U>Delete</U></HTML>");
		GUI.SatDeleteLink.setText("<HTML><U>Delete</U></HTML>");
		int x = 0;
		while (x < GUI.RoverBtns.length){
			GUI.RoverBtns[x].setCursor(new Cursor(Cursor.HAND_CURSOR));
			GUI.SatBtns[x].setCursor(new Cursor(Cursor.HAND_CURSOR));
			x++;
		}
	}

	
	// FILE COMMUNICATION
	
	private void ReadPhoto(int length){
		if (receivingPhoto){
			GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Waiting for Image.\n");
			try {
				while (inputStream.available() <= 0) {}
					Thread.sleep((int)(20 / Globals.getTimeScale()));
					GUI.SerialDisplayLbl.setText(GUI.SerialDisplayLbl.getText() + "Receiving Image.\n");
					// writeToLog("Receiving Image");
					String text = GUI.SerialDisplayLbl.getText();
					byte[] bytes = new byte[length];
					char[] progress = new char[length / 500 + 1];
					int index = 0;
					while (index < progress.length){
						progress[index] = '-';
						index++;
					}
					progress[0] = '>';
					GUI.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
					index = 0;
					int escape = 0;
					while (index < length){
						while(inputStream.available() > 0) {
							escape = 0;
							try {
								bytes[index] = (byte) inputStream.read();
							}
							catch (ArrayIndexOutOfBoundsException e){
								break;
							}
							if (index % 500 == 0 && index != 0){
								progress[index / 500 - 1] = '-';
								progress[index / 500] = '>';
								GUI.SerialDisplayLbl.setText(text + buildString(progress, 0, progress.length - 1));
							}
							index++;
						}
						escape++;
						if (escape > Integer.MAX_VALUE - 100){
							break;
						}
					}
					if (index == length){
						File image = new File("");
						image = new File(image.getAbsoluteFile() + "\\Photos\\IMAGE " + DateTime.toString("MMddhhmm") + ".jpg");
						FileOutputStream fos = new FileOutputStream(image);
						fos.write(bytes);
						receivedFiles = Augment(receivedFiles, image.getAbsolutePath());
						fos.close();
						// writeToLog("Recieved Image.  Stored in: " + image.getAbsolutePath());
						receivingPhoto = false;
						GUI.SerialDisplayLbl.setText(text + "Done.\n");
						GUI.MailBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Mail_Message.png")));
					}
					else {
						GUI.SerialDisplayLbl.setText(text + "Image transfer failed, incomplete size requirement.\n");
					}
			}	
			catch(Exception exe) {
				exe.printStackTrace();
			}
		}
	}
	
	public void OpenRecievedFiles(){
		if (receivedFiles.length > 0){
			String[] choices = new String[receivedFiles.length];
			int x = 0;
			while (x < choices.length){
				choices[x] = getFileName(receivedFiles[x]);
				x++;
			}
			int choice = (new PopUp()).showOptionDialog("Select a File", "Open File", choices);
			if (choice != -1){
				File image = new File(receivedFiles[choice]);
				try {
					final BufferedImage img = ImageIO.read(image);
					new ThreadTimer(0, new Runnable(){
						public void run(){
							PopUp opane = new PopUp();
							opane.setCustomButtonOptions(new String[] { "Save", "Close" }, new int[] { 0, 1 });
							int choice = opane.showPictureDialog(new ImageIcon(img), "Received Image", PopUp.CUSTOM_OPTIONS);
							if (choice == 0){
								javax.swing.JFileChooser browse = new javax.swing.JFileChooser();
								browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG file", "jpg", "jpeg"));
								browse.showSaveDialog(GUI);
								try {
									String filepath = browse.getSelectedFile().getAbsolutePath() + ".jpeg";
									File imageOut = new File(filepath);
									try {
										ImageIO.write(img, "jpg", imageOut);
									}
									catch (Exception e){
										opane.showConfirmDialog("Something went worng and the file failed to save.", "IO Error", PopUp.DEFAULT_OPTIONS);
									}
									
								} catch (Exception e) {}								
							}
						}
					}, 1);
					receivedFiles = Remove(receivedFiles, choice);
					if (receivedFiles.length == 0){
						GUI.MailBtn.setIcon(new ImageIcon(InterfaceForm.class.getResource("/Mail.png")));
					}
				} catch (Exception e) {}
			}
		}
		else {
			new ThreadTimer(0, new Runnable() {
				public void run(){
					(new PopUp()).showConfirmDialog("There are no unread files.", "Received Files", PopUp.DEFAULT_OPTIONS);
				}
			}, 1);
		}
	}
	
	private String getFileName(String filepath){
		char[] chars = filepath.toCharArray();
		int x = chars.length - 1;
		while (x >= 0){
			if (chars[x] == '\\'){
				break;
			}
			x--;
		}
		x++;
		String out = "";
		while (x < chars.length){
			out += chars[x];
			x++;
		}
		return out;
	}
	
	
	// FILE STUFF
	
	private void SaveProgrammer(){
		String filename = "CommandString.dll";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			File file = new File("");
			file = new File(file.getAbsolutePath() + "\\CommandString.dll");
			file.delete();
		}
		catch (Exception e) {}
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(new SaveFile(actionCommands, actionTips, actionIcons));
			out.close();
		}
		catch (Exception ex) {
			HandleError(ex);
		}
	}
	
	/*public void writeToLog(String what){
		try {
			LogFile.write(what + "\t\t" + DateTime.toString());
			LogFile.write(System.getProperty("line.separator"));
		}
		catch (Exception e) {
			HandleError(e);
		}
	}*/
	
	
	// SUPPORTING METHODS
	
	private String buildString(char[] array, int start, int end){
		String out = "";
		while (start <= end){
			try {
				out += array[start] + "";
			}
			catch (Exception e){
				return "";
			}
			start++;
		}
		return out;
	}
	
	private String[] Augment(String[] array, String val){
		String[] out = new String[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private String[] Remove(String[] array, int which){
		String[] out = new String[array.length - 1];
		int x = 0;
		while (x < which){
			out[x] = array[x];
			x++;
		}
		x++;
		while (x < array.length){
			out[x - 1] = array[x];
			x++;
		}
		return out;
	}
	
	public void HandleError(Exception e){
		e.printStackTrace();
		JOptionPane.showConfirmDialog(GUI, "The program broke, restart and tell Zac.", "A Fatal Error Occured", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		// writeToLog("The GUI encountered and Error");
		//try {
			// LogFile.write(e.getMessage());
			// LogFile.write(System.getProperty("line.separator"));
		//} catch (IOException e1) {}
		System.exit(0);
		
	}
}
