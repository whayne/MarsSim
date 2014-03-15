package roverMock;

import objects.Globals;

public class RoverCode {

	static RoverForm GUI = new RoverForm();
	
	private int tries = 0;
	private int imageSize;
	// File imgFile;
	// File myFile;
	private char[] filename;
	private boolean connection = false;
	private boolean mute = false;
	
	private char tag = '\0';
	private char[] data = new char[20];
	private int index = 0;
	
	static void align(){
		GUI = RoverForm.frame;
	}
	
	public void runCode(){
		while (true){
		System.out.print(""); // Don't know why but fails without this.
		try {
		if (Globals.RFAvailable('r') > 0){   
		    if ((char)Globals.ReadSerial('r') == 'r'){  
		    	Thread.sleep((int)(500 / Globals.getTimeScale()));
			    System.out.println("here");
		    	Globals.ReadSerial('r');
			    tag = (char)Globals.ReadSerial('r');
			    if (Globals.RFAvailable('r') > 0) {
			    	data[0] = tag;
			    	index++;
			    	while (Globals.RFAvailable('r') > 0){
			    		data[index] = (char)Globals.ReadSerial('r');
			          	index++;
			        }
			        while (index < 20){
			        	data[index] = '\0';
			        	index++;
			        }        
			        sendSerial("s r %");
			        if (data.equals("move")){
			          //motor1->run(FORWARD);
			          //motor2->run(FORWARD);
			          //motor3->run(FORWARD);
			          //motor4->run(FORWARD);
			          //delay(1000); //For ir sensor testing
			        }        
			        else if (data.equals("stop")){
			          //motor1->run(RELEASE);
			          //motor2->run(RELEASE);
			          //motor3->run(RELEASE);
			          //motor4->run(RELEASE); 
			        }
			        else if (data.equals("spin_ccw")) {
			          //motor1->run(BACKWARD);
			          //motor2->run(BACKWARD);
			          //motor3->run(FORWARD);
			          //motor4->run(FORWARD);
			        }
			        else if (data.equals("spin_cw")) {
			          //motor1->run(FORWARD);
			          ///motor2->run(FORWARD);
			          //motor3->run(BACKWARD);
			          //motor4->run(BACKWARD); 
			        }
			        else if (data.equals("backward")) {
			          //motor1->run(BACKWARD);
			          //motor2->run(BACKWARD);
			          //motor3->run(BACKWARD);
			          //motor4->run(BACKWARD); 
			        }
			        else if (data.equals("photo")) {
			          /*delay(2000);
			          sendSerial("s r }");
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
			            
			            Serial.print("s c [o]");
			            delay(1000);
			            delay(1000);
			            myFile = SD.open(filename, FILE_READ);
			            if (myFile) {
			              // read from the file until there's nothing else in it:
			              while (myFile.available()) {
			      	        index = 0;
			                while (index < 60 && myFile.available()){
			                  sendSerial((char)myFile.read());
			                  index++;
			                }
			                delay(2000);
			              }
			              // close the file:
			              myFile.close();
			              cam.begin();
			              cam.setImageSize(imageSize);
			            }
			          }
			          else {
			            delay(1000);
			            Serial.print("s r n Picture failed to take.");
			          }*/
			        }
			      }
			      else if (tag == '#'){
			    	  //Satilite Confirmation
			      }
			      else if (tag == '^'){
			    	  connection = true;
			    	  sendSerial("s r ^");
			      }
			      else if (tag == '}'){
			    	  mute = true;
			      }
			      else if (tag == '{'){
			    	  mute = false;
			      }
			      data = new char[20];
			      index = 0;
			      tag = '\0';
		    }
		}
		} catch (Exception e) {
			System.out.println("Error in Rover Run Code");
		}
		}
	}
	
	boolean sendSerial(String mess){
		char[] message = mess.toCharArray();
		if (!mute){
			int x = 0;
			while (x < message.length){
				if (message[x] == '\0'){
					break;
				}
				Globals.writeToSerial(message[x], 'r'); // Write to Serial one char at a time
				try {
					Thread.sleep((int)(20 / Globals.getTimeScale())); // Pause for sending
				} catch (InterruptedException e) {}
				x++;
			}
			return true;
		}
		return false;
	}

	boolean sendSerial(char mess){
		if (!mute){
			Globals.writeToSerial(mess, 'r');
			return true;
		}
		return false;
	}
	
}
