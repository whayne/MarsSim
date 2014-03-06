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
	private ThreadTimer loopFunction;
	
	
	// private int imageSize;
	// private String filename;
	private char[] data = new char[20];
	private int index = 2;
	private char tag = '\0';
	
	static void align(){
		GUI = SatelliteForm.frame;
	}
	
	public void browseForINOFile(){
		JFileChooser browse = new JFileChooser();
		browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arduino Code", "ino"));
		browse.showOpenDialog(GUI);
		String filepath = browse.getSelectedFile().getAbsolutePath();
		GUI.FileLocationTxt.setText(filepath);
		newFileConnected();
	}
	
	public void newFileConnected(){
		try {
			loopFunction.Stop();
		} catch (Exception e) {}
		if (GUI.FileLocationTxt.getText().equals("")){
			return;
		}
		try {
			Scanner file = new Scanner(new File(GUI.FileLocationTxt.getText()));
			while (file.hasNextLine()){
				//System.out.println(file.nextLine());
				//TODO interpret text to commands
				
			}
			loopFunction  = new ThreadTimer(2, new Runnable(){
				public void run(){
					Runnable[] cmds = new Runnable[0]; // The interpreted list of commands
					int x = 0;
					while (x < cmds.length){
						cmds[x].run(); //run each command in order
						x++;
					}
				}
			}, ThreadTimer.FOREVER); // Do it again until stopped
		} 
		catch (FileNotFoundException e) {
			JOptionPane.showConfirmDialog(GUI, "The File Could Not be found.", "Invalid File", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	
	
	public void runCode(){
		while (true){
		try {
		Thread.sleep((int)(500 / Globals.getTimeScale()));
	    if ((char)Globals.ReadSerial('s') == 's'){
	      Thread.sleep((int)(500 / Globals.getTimeScale()));
	      Globals.ReadSerial('s');
	      tag = (char) Globals.ReadSerial('s');
	      Globals.ReadSerial('s');
	      data[1] = ' ';
	      while (Globals.RFAvailable('s') > 0){
	        data[index] = (char)Globals.ReadSerial('s');
	        index++;
	      }
	      while (index < 20){
	        data[index] = '\0';
	        index++;
	      }
	      if (tag == 'g'){
	        data[0] = 'r';
	        SerialPrint(data);
	        Thread.sleep((int)(1000 / Globals.getTimeScale()));
	        if (data[2] != '^'){
	          SerialPrint("g #");
	        }
	      }
	      if (tag == 'r'){
	        data[0] = 'g';
	        SerialPrint(data);
	        Thread.sleep((int)(1000 / Globals.getTimeScale()));
	        if (data[2] != '^' && data[2] != '%'){
	          SerialPrint("r #");
	        }
	      }
	      if (tag == 'c'){
	        SerialPrint("g #");
	        index = 0;
	        while (index < 18){
	          data[index] = data[index + 2];
	          index++;
	        }
	        data[index] = '\0';
	        index++;
	        data[index] = '\0';
	        index++;
	        if (data.equals("photo")){
	          /*Thread.sleep((int)(300 / Globals.getTimeScale()));
	          SerialPrint("r }");
	          cam.setImageSize(imageSize);
	          if (cam.takePicture()){
	            filename = new char[13];
	            strcpy(filename, "PIC0000.JPG");
	            for (int i = 0; i < 100000; i++){
	              filename[5] = '0' + i/10;
	              filename[6] = '0' + i%10;
	              if (! SD.exists(filename)){
	                break;
	              }
	            }
	            
	            imgFile = SD.open(filename, FILE_WRITE);
	            uint16_t jpglen = cam.frameLength();
	            uint16_t length = jpglen;
	            byte wCount = 0; // For counting # of writes
	            while (jpglen > 0) {
	              // read 32 bytes at a time;
	              uint8_t *buffer;
	              uint8_t bytesToRead = min(32, jpglen); // change 32 to 64 for a speedup but may not work with all setups!
	              buffer = cam.readPicture(bytesToRead);
	              imgFile.write(buffer, bytesToRead);
	              if(++wCount >= 64) { // Every 2K, give a little feedback so it doesn't appear locked up
	                wCount = 0;
	              }
	              jpglen -= bytesToRead;
	            }
	            imgFile.close();
	            
	            SerialPrint("g i ");
	            SerialPrint(length);
	            Thread.sleep((int)(1000 / Globals.getTimeScale()));
	            myFile = SD.open(filename, FILE_READ);
	            if (myFile) {
	              // read from the file until there's nothing else in it:
	              while (myFile.available()) {
	      	        index = 0;
	                while (index < 64 && myFile.available()){
	                  Serial.write(myFile.read());
	                  index++;
	                }
	                Thread.sleep((int)(1000 / Globals.getTimeScale()));
	              }
	              // close the file:
	              myFile.close();
	              cam.begin();
	              cam.setImageSize(imageSize);
	              SerialPrint("r {");
	            }
	          }
	          else {
	            SerialPrint("g n Picture failed to take.");
	          } */
	        }
	        else if (data.equals("[o]")){
	          /*char filename[13];
	          strcpy(filename, "PIC0000.JPG");
	          for (int i = 0; i < 10000; i++) {
	            filename[4] = '0' + i/10;
	            filename[5] = '0' + i%10;
	            // create if does not exist, do not open existing, write, sync after write
	            if (! SD.exists(filename)) {
	              break;
	            }
	          }
	          myFile = SD.open(filename, FILE_WRITE);
	          index = 0;
	          uint8_t buffer;
	          while (Globals.RFAvailable('s') == 0) {}
	          Thread.sleep((int)(1000 / Globals.getTimeScale()));
	          while (Globals.RFAvailable('s') > 0){
	            while (Globals.RFAvailable('s') > 0){
	              buffer = Globals.ReadSerial('s');
	              // Serial.write(buffer);
	              myFile.write(buffer);
	              index++;
	            }
	            Thread.sleep((int)(2000 / Globals.getTimeScale()));
	          }
	          myFile.close();
	          
	          SerialPrint("g {");
	          Thread.sleep((int)(500 / Globals.getTimeScale()));
	          SerialPrint("r }");
	          Thread.sleep((int)(500 / Globals.getTimeScale()));
	          myFile = SD.open(filename, FILE_READ);
	          SerialPrint("g i ");
	          SerialPrint(index);
	          Thread.sleep((int)(1000 / Globals.getTimeScale()));
	          index = 0;
	          myFile = SD.open(filename, FILE_READ);
	          if (myFile) {
	            // read from the file until there's nothing else in it:
	            while (myFile.available()) {
	      	      index = 0;
	              while (index < 60 && myFile.available()){
	                Serial.write(myFile.read());
	                index++;
	              }
	              Thread.sleep((int)(1000 / Globals.getTimeScale()));
	            }
	            // close the file:
	            myFile.close();
	            cam.begin();
	            cam.setImageSize(imageSize);
	            SerialPrint("r {");
	          } */
	        }
	        else {
	          // SerialPrint("\nrecieved: ");
	          // Serial.println(data);
	        }
	        // ...
	      }
	      data = new char[20];
	      index = 2;
	      tag = '\0';
	    }
	    else {
	      while (Globals.RFAvailable('s') > 0){
	        Globals.ReadSerial('s');
	      }
	    }}
	    catch (Exception e) {
	    	System.out.println("Error in Satellite Code Run");
	    }		
		}
	}
	
	private void SerialPrint(String msg){
		char[] output = msg.toCharArray();
		int x = 0;
		while (x < output.length){
			Globals.writeToSerial(output[x], 's'); // Write to Serial one char at a time
			try {
				Thread.sleep((int)(20 / Globals.getTimeScale())); // Pause for sending
			} catch (InterruptedException e) {}
			x++;
		}
	}
	
	private void SerialPrint(char[] msg){
		String message = "";
		int x = 0;
		while (x < msg.length){
			if (msg[x] == '\0'){
				break;
			}
			message += msg[x];
			x++;
		}
		SerialPrint(message);
	}
	
}
