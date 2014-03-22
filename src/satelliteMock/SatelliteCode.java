package satelliteMock;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import objects.Globals;
import objects.ThreadTimer;

public class SatelliteCode {

	static SatelliteForm GUI = new SatelliteForm();

	static void align(){
		GUI = SatelliteForm.frame;
	}
	

	private final int SDpin = 4;

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
					delay(300);
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
						if (data[2] != '^'){
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
						while (index < 18){
							data[index] = data[index + 2];
							index++;
						}
						data[index] = '\0';
						index++;
						data[index] = '\0';
						index++;
						if (strcmp(data, "photo") == 0){
							delay(500);
							//takePhoto();
						}
						else if (strcmp(data, "[o]") == 0){
							/*char[] filename = new char[] { 'P', 'I', 'C', '0', '0', '0', '0', '.', 'J', 'P', 'G' };
							strcpy(filename, );
							int i = 0;
							while (i < 10000){
								filename[4] = (char) ('0' + i/10);
								filename[5] = (char) ('0' + i%10);
	            				// 	create if does not exist, do not open existing, write, sync after write
								if (! SD.exists(filename)) {
									break;
								}
								i++;
							}
							myFile = SD.open(filename, FILE_WRITE);
							index = 0;
							uint8_t buffer;
							while (Globals.RFAvailable('s') == 0) {}
							delay(1000);
							while (Globals.RFAvailable('s') > 0){
								while (Globals.RFAvailable('s') > 0){
									buffer = Globals.ReadSerial('s');
	              // 	Serial.write(buffer);
									myFile.write(buffer);
									index++;
								}
								delay(2000);
							}
							myFile.close();
							
							sendSerial("r }");
							delay(1200);
							sendSerial("g {");
							delay(1000);
							myFile = SD.open(filename, FILE_READ);
							sendSerial("g i ");
							sendSerial(index);
							delay(1000);
							index = 0;
							myFile = SD.open(filename, FILE_READ);
							if (myFile) {
	            // 	read from the file until there's nothing else in it:
								while (myFile.available()) {
									index = 0;
									while ((index < 60) && (myFile.available())){
										Serial.write(myFile.read());
										index++;
									}
									delay(1000);
								}
								myFile.close();
								cam.begin();
								cam.setImageSize(imageSize);
								sendSerial("r {");
							}*/
						}
						else if (strcmp(data, "instructions") == 0){
							/*if (SD.exists("instruct.txt")){
								SD.remove("instruct.txt");
							}
							myFile = SD.open("instruct.txt", FILE_WRITE);
							if (!myFile){
								sendSerial("g n File Failed to Open");
							}
							while (Globals.RFAvailable('s') == 0) {}
							delay(1000);
							while (Globals.RFAvailable('s') > 0){
								while (Globals.RFAvailable('s') > 0){
									myFile.write((char)Globals.ReadSerial('s'));
								}
								delay(2000);
							}
							myFile.close();
							hasInstructions = true;
							instructsComplete = 0;
							sendSerial("g }");
							delay(700);
							sendSerial("r {");
							delay(700);
							sendSerial("r instructions");
							delay(1000);
							myFile = SD.open("instruct.txt", FILE_READ);
							while (myFile.available()) {
								index = 0;
								while ((index < 60) && (myFile.available())){
									Serial.write((char)myFile.read());
									index++;
								}
								delay(2000);
							}
							myFile.close();*/
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
			
			/*if (millis() - timeSinceCmd > 60000 && hasInstructions){
				myFile = SD.open("instruct.txt", FILE_READ);
				char* cmd;
				int x = 0;
				int count = 0;
				while (count <= instructsComplete){
					cmd = new char[20];
					x = 0;
					while (myFile.peek() != '\n'){
						cmd[x] = myFile.read();
						x++;
					}
					cmd[x] = '\0';
					myFile.read();
					count++;
				}
				
				if (cmd[0] == 's'){
					x = 0;
					while (x < strlen(cmd)-2){
						cmd[x] = cmd[x+2];
						x++;
					}
					cmd[x] = '\0';
					if (strcmp(cmd, "photo") == 0){
	         // 	takePicture();
					}
					else if (strcmp(cmd, "report") == 0){
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
				myFile.close();
			}*/
			}
			catch (Exception e){
				System.out.println("Error in Satellite Code");
			}
		}
	}
		
	/*void takePhoto(){
	  sendSerial("r }");
	  cam.setImageSize(imageSize);
	  if (cam.takePicture()){
	    filename = new char[13];
	    strcpy(filename, "PIC0000.JPG");
	    int i = 0;
	    while (i < 100000){
	      filename[5] = '0' + i/10;
	      filename[6] = '0' + i%10;
	      if (! SD.exists(filename)){
	        break;
	      }
	      i++;
	    }
	        
	    imgFile = SD.open(filename, FILE_WRITE);
	    uint16_t jpglen = cam.frameLength();
	    uint16_t length = jpglen;
	    byte wCount = 0; // For counting # of writes
	    while (jpglen > 0) {
	      // read 32 bytes at a time;
	      uint8_t* buffer;
	      uint8_t bytesToRead = min(32, jpglen); // change 32 to 64 for a speedup but may not work with all setups!
	      buffer = cam.readPicture(bytesToRead);
	      imgFile.write(buffer, bytesToRead);
	      wCount++;
	      if(wCount >= 64) { // Every 2K, give a little feedback so it doesn't appear locked up
	        wCount = 0;
	      }
	      jpglen -= bytesToRead;
	   }
	   imgFile.close();
	            
	   sendSerial("g i ");
	   sendSerial(length);
	   delay(1000);
	   myFile = SD.open(filename, FILE_READ);
	   if (myFile) {
	      // read from the file until there's nothing else in it:
	      while (myFile.available()) {
	        index = 0;
	        while ((index < 64) && (myFile.available())){
	          Serial.write(myFile.read());
	          index++;
	        }
	        delay(1000);
	      }
	      // close the file:
	      myFile.close();
	      cam.begin();
	      cam.setImageSize(imageSize);
	      sendSerial("r {");
	    }
	  }
	  else {
	    delay(1000);
	    sendSerial("g n Picture failed to take.");
	  }
	}*/
	
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
			Globals.writeToSerial(message[x], 'r'); // Write to Serial one char at a time
			print += message[x];
			try {
				Thread.sleep((int)(20 / Globals.getTimeScale())); // Pause for sending
			} catch (InterruptedException e) {}
			x++;
		}
		GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText() + print + "\t\t" + (System.currentTimeMillis()-Globals.startTimeMillis) + "\n");
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
}