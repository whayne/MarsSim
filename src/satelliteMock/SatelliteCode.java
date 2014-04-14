package satelliteMock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import objects.Globals;
import objects.ThreadTimer;
import roverMock.RoverCode;

public class SatelliteCode {

	static SatelliteForm GUI = new SatelliteForm();

	static void align(){
		GUI = SatelliteForm.frame;
	}	

	private final int SDpin = 4;

	private String instructions = "";
	private boolean hasInstructions = false;
	private int instructsComplete = 0;
	private long timeSinceCmd = 0;

	private int imageSize;
	private File imgFile;
	private File myFile;
	private char[] filename = new char[13];
	
	private char[] data = new char[30];
	private int index = 2;
	private char tag = '\0';

	public void runCode(){
		while (true){
			System.out.print(""); // Don't know why but fails without this.
			try {
			if (Globals.RFAvailable('s') > 0){
				if (Globals.ReadSerial('s') == 's'){
					delay(1000);
					Globals.ReadSerial('s');
					tag = (char) Globals.ReadSerial('s');
					Globals.ReadSerial('s');
					data[1] = ' ';
					while (Globals.RFAvailable('s') > 0){
						data[index] = (char) Globals.ReadSerial('s');
						index++;
					}
					while (index < 30){
						data[index] = '\0';
						index++;
					}
					if (tag == 'g'){
						data[0] = 'r';
						sendSerial(data);
						delay(1000);
						if (data[2] != '^' && data[2] != '}' && data[2] != '{'){
							sendSerial("g #");
						}
					}
					if (tag == 'r'){
						data[0] = 'g';
						sendSerial(data);
						delay(1000);
						if (data[2] != '^' && data[2] != '%' && data[2] != '}' && data[2] != '{'){
							sendSerial("r #");
						}
					}
					if (tag == 'c'){
						index = 0;
						while (index < data.length - 2){
							data[index] = data[index + 2];
							index++;
						}
						if (strcmp(data, "photo") == 0){
							sendSerial("g #");
							delay(600);
							takePhoto();
						}
						else if (strcmp(data, "[o]") == 0){
							byte[] data = new byte[0];
							while (Globals.RFAvailable('s') == 0) {
								delay(5);
							}
							delay(1000);
							int index = 0;
							while (Globals.RFAvailable('s') > 0) {
								while (Globals.RFAvailable('s') > 0) {
									data = Augment(data, Globals.ReadSerial('s'));
									index++;
								}
								delay(2000);
							}
							sendSerial("r }");
							delay(1200);
							sendSerial("g {");
							delay(1000);
							sendSerial("g i " + index);
							delay(1000);
							index = 0;
							int x = 0;
							while (x < data.length) {
								index = 0;
								while ((index < 60) && x < data.length) {
									Globals.writeToSerial(data[x], 's');
									index++;
									x++;
								}
								delay(1000);
							}
							sendSerial("r {");
						}
						else if (strcmp(data, "instructions") == 0){
							instructions = "";
							while (Globals.RFAvailable('s') == 0) {
								delay(5);
							}
							delay(1000);
							while (Globals.RFAvailable('s') > 0){
								while (Globals.RFAvailable('s') > 0){
									instructions += (char)Globals.ReadSerial('s');
									try {
										Thread.sleep(1);
									} catch (Exception e) {}
								}
								delay(2000);
							}
							hasInstructions = true;
							instructsComplete = 0;
							sendSerial("g }");
							delay(700);
							sendSerial("r {");
							delay(700);
							sendSerial("r instructions");
							delay(1000);
							int x = 0;
							while (x < instructions.length()) {
								index = 0;
								while ((index < 60) && (x < instructions.length())){
									Globals.writeToSerial(instructions.charAt(x), 's');
									index++;
									x++;
								}
								delay(2000);
							}
						}
						else {
							//sendSerial("\nrecieved: ");
							//Serial.println(data);
						}
						// 	...
					}
					data = new char[30];
					index = 2;
					tag = '\0';
					timeSinceCmd = System.currentTimeMillis();
				}
				else {
					while (Globals.RFAvailable('s') > 0){
						Globals.ReadSerial('s');
					}
				}
			}
			
			if (System.currentTimeMillis() - timeSinceCmd > 60000 && hasInstructions){
				String cmd = "";
				int x = 0;
				int count = 0;
				while (count <= instructsComplete){
					cmd = "";
					while (instructions.charAt(x) != '\n'){
						cmd += instructions.charAt(x);
						x++;
					}
					x++;
					count++;
				}
				
				if (cmd.charAt(0) == 's'){
					String temp = cmd;
					cmd = "";
					x = 2;
					while (x < temp.length()){
						cmd += temp.charAt(x);
						x++;
					}
					if (cmd.equals("photo")){
						takePhoto();
					}
					else if (cmd.equals("report")){
						if (sendSerial("g n Sat Instructs Done")) {
							hasInstructions = false;
							instructsComplete = 0;
						}
						else {
							instructsComplete--;
						}
					}
				}
				instructsComplete++;
			}
			}
			catch (Exception e){
				System.out.println("Error in Satellite Code");
				e.printStackTrace();
			}
		}
	}
		
	void takePhoto(){
	  try {
		  sendSerial("r }");
		  InputStream data = RoverCode.class.getResourceAsStream("/Satellite Sample.jpg");
		  delay(2000);
		  sendSerial("g i 12764"); //magic length
		  delay(2000);
		  int hold = data.read();
		  int index;
		  int x = 0;
		  while (hold != -1) {
			  index = 0;
			  while (index < 60 && hold != -1) {
				  Globals.writeToSerial((byte) hold, 's');
				  hold = data.read();
				  index++;
				  x++;
			  }  
			  delay(1000);
		  }
		  delay(500);
		  sendSerial("r {");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void delay(int length){
		try {
			Thread.sleep((int)(length/Globals.getTimeScale()));
		}
		catch (Exception e) {}
	}

	boolean sendSerial(String mess){
		return sendSerial(mess.toCharArray());
	}

	boolean sendSerial(char[] message){
		String print = "";
		int x = 0;
		while (x < message.length){
			if (message[x] == '\0'){
				break;
			}
			Globals.writeToSerial(message[x], 's'); // Write to Serial one char at a time
			print += message[x];
			try {
				Thread.sleep((int)(5 / Globals.getTimeScale())); // Pause for sending
			} catch (InterruptedException e) {}
			x++;
		}
		GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText() + print + "\t\t\t" + (System.currentTimeMillis()-Globals.startTimeMillis) + "\n");
		return true;
	}

	boolean sendSerial(char mess){
		Globals.writeToSerial(mess, 'r');
		GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText() + mess + "\t\t\t" + (System.currentTimeMillis()-Globals.startTimeMillis) + "\n");
		return true;
	}
	
	private int strcmp(char[] first, String second){
		try {
			char[] sec = second.toCharArray();
			int count = 0;
			int x = 0;
			while (first[x] != '\0'){
				if (first[x] != sec[x]){
					return 1;
				}
				x++;
			}
			return 0;
		}
		catch (Exception e){
			return 1;
		}
	}
	
	private byte[] Augment(byte[] array, byte val){
		byte[] out = new byte[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
}