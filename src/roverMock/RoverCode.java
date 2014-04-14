package roverMock;

import interfaceGUI.InterfaceForm;

import java.awt.Toolkit;
import java.io.InputStream;

import objects.Globals;
import objects.ThreadTimer;
import simulatorWrapper.WrapperEvents;

public class RoverCode {

	static RoverForm GUI = new RoverForm();
	private RoverDriveModel DriveModel = new RoverDriveModel();
	
	private int tries = 0;
	private int imageSize;
	private char[] filename;
	private boolean connection = false;
	private boolean mute = false;
	boolean moving = false;
	private String motorState = "";

	private boolean hasInstructions = false;
	private String instructions = "";
	private int instructsComplete = 0;
	private long timeSinceCmd = 0;
	private boolean waiting = false;
	private long waitTime = 0;

	private float boardVoltage = 9;
	private float motorVoltage = 12;
	private float armVoltage = 0;
	private float R1 = 10000.0f;
	private float R2 = 3300.0f;

	private char tag = '\0';
	private char[] data = new char[20];
	private int index = 0;
	private boolean go = true;

	
	static void align() {
		GUI = RoverForm.frame;
	}

	public void runCode() {
		while (true) {
			System.out.print(""); // Don't know why but fails without this.
			try {
				/*
				 * for (pos = 0; pos < 180; pos += 1) { pan.write(pos);
				 * delay(100); } for (pos = 180; pos>= 1; pos -=1) {
				 * pan.write(pos); delay(100); }
				 */

				/*
				 * if (digitalRead(button) == HIGH) {
				 * sendSerial("s r n TESTING"); digitalWrite(LED, HIGH);
				 * delay(500); digitalWrite(LED, LOW); }
				 */

				if (Globals.RFAvailable('r') > 0) {
					if (Globals.ReadSerial('r') == 'r' && go) {
						delay(500);
						Globals.ReadSerial('r');
						tag = (char) Globals.ReadSerial('r');
						if (Globals.RFAvailable('r') > 0) {
							data[0] = tag;
							index++;
							while (Globals.RFAvailable('r') > 0 && index < 30) {
								data[index] = (char) Globals.ReadSerial('r');
								index++;
							}
							data[index] = '\0';
							if (strcmp(data, "move") == 0) {
								sendSerial("s r %");
								DriveModel.setMotorState(0, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(1, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(2, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(3, RoverDriveModel.FORWARD);
								moving = true;
								delay(1000); // For ir sensor testing
							} 
							else if (strcmp(data, "stop") == 0) {
								sendSerial("s r %");
								DriveModel.setMotorState(0, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(1, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(2, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(3, RoverDriveModel.RELEASE);
								moving = true;
							} 
							else if (strcmp(data, "spin_ccw") == 0) {
								sendSerial("s r %");
								DriveModel.setMotorState(0, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(1, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(2, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(3, RoverDriveModel.FORWARD);
								moving = true;
							} 
							else if (strcmp(data, "spin_cw") == 0) {
								sendSerial("s r %");
								DriveModel.setMotorState(0, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(1, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(2, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(3, RoverDriveModel.BACKWARD);
								moving = true;
							} 
							else if (strcmp(data, "backward") == 0) {
								sendSerial("s r %");
								DriveModel.setMotorState(0, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(1, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(2, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(3, RoverDriveModel.BACKWARD);
								moving = true;
							} 
							else if (strcmp(data, "getvolts") == 0) {
								sendSerial("s r %");
								// updateVoltages();
								delay(2000);
								sendSerial("s r Vrov=");
								sendSerial(boardVoltage);
								delay(2500);
								sendSerial("s r Vmtr=");
								sendSerial(motorVoltage);
								delay(2500);
								sendSerial("s r Varm=");
								sendSerial(armVoltage);
							} 
							else if (strcmp(data, "photo") == 0) {
								sendSerial("s r %");
								delay(2000);
								takePicture();
							} 
							else if (strcmp(data, "instructions") == 0) {
								instructions = "";
								while (Globals.RFAvailable('r') == 0) {
									delay(5);
								}
								delay(1000);
								while (Globals.RFAvailable('r') > 0) {
									while (Globals.RFAvailable('r') > 0) {
										instructions += (char) Globals
												.ReadSerial('r');
									}
									delay(2000);
								}
								sendSerial("s r {");
								delay(1500);
								sendSerial("s r n Instructions Transfered");
								hasInstructions = true;
								instructsComplete = 0;
							}
						} 
						else if (tag == '#') {
							// Satilite Confirmation
						} 
						else if (tag == '^') {
							connection = true;
							sendSerial("s r ^");
						} 
						else if (tag == '}') {
							mute = true;
							// digitalWrite(muteLED, HIGH);
						} 
						else if (tag == '{') {
							mute = false;
							// digitalWrite(muteLED, LOW);
						}
						data = new char[30];
						index = 0;
						tag = '\0';
						timeSinceCmd = System.currentTimeMillis();
						if (moving){
					        waitTime += System.currentTimeMillis() + 60000;
						}
					} 
					else {
						delay(300);
						go = false;
						int waiting = Globals.RFAvailable('r');
						while (waiting >= 0) {
							Globals.ReadSerial('r');
							waiting--;
						}
					}
				} 
				else {
					go = true;
				}

				if (System.currentTimeMillis() - timeSinceCmd > 60000 && hasInstructions && !mute) {
					if (!waiting || (System.currentTimeMillis() - waitTime > 1000)) {
						waiting = false;
						String cmd = "";
						int x = 0;
						int count = 0;
						while (count <= instructsComplete) {
							cmd = "";
							while (instructions.charAt(x) != '\n') {
								cmd += instructions.charAt(x);
								x++;
							}
							x++;
							count++;
						}
						if (cmd.charAt(0) == 'r') {
							String temp = cmd;
							cmd = "";
							x = 2;
							while (x < temp.length()){
								cmd += temp.charAt(x);
								x++;
							}
							// System.out.println(cmd + " - " + System.currentTimeMillis());
							if (cmd.equals("move")) {
								DriveModel.setMotorState(0, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(1, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(2, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(3, RoverDriveModel.FORWARD);
								moving = true;
								motorState = cmd;
							} 
							else if (cmd.equals("backward")) {
								DriveModel.setMotorState(0, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(1, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(2, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(3, RoverDriveModel.BACKWARD);
								moving = true;
								motorState = cmd;
							} 
							else if (cmd.equals("spin_cw")) {
								DriveModel.setMotorState(0, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(1, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(2, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(3, RoverDriveModel.BACKWARD);
								moving = true;
								motorState = cmd;
							} 
							else if (cmd.equals("spin_ccw")) {
								DriveModel.setMotorState(0, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(1, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(2, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(3, RoverDriveModel.FORWARD);
								moving = true;
								motorState = cmd;
							} 
							else if (cmd.equals("stop")) {
								DriveModel.setMotorState(0, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(1, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(2, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(3, RoverDriveModel.RELEASE);
								moving = false;
							} 
							else if (cmd.equals("photo")) {
								takePicture();
							}
							else if (cmd.equals("delay1")) {
								waiting = true;
								waitTime = System.currentTimeMillis();
							}
							else if (cmd.equals("report")) {
								if (sendSerial("s r n Rover Instructs Done")) {
									hasInstructions = false;
									instructsComplete = 0;
								}
								else {
									instructsComplete--;
								}
							}
						}
						instructsComplete++;
						delay(5);
					}
					else if (waiting){
					   if (moving){
					  	   if (motorState.equals("move")) {
								DriveModel.setMotorState(0, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(1, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(2, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(3, RoverDriveModel.FORWARD);
							} 
							else if (motorState.equals("backward")) {
								DriveModel.setMotorState(0, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(1, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(2, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(3, RoverDriveModel.BACKWARD);
							} 
							else if (motorState.equals("spin_cw")) {
								DriveModel.setMotorState(0, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(1, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(2, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(3, RoverDriveModel.BACKWARD);
							} 
							else if (motorState.equals("spin_ccw")) {
								DriveModel.setMotorState(0, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(1, RoverDriveModel.BACKWARD);
								DriveModel.setMotorState(2, RoverDriveModel.FORWARD);
								DriveModel.setMotorState(3, RoverDriveModel.FORWARD);
							} 
							else if (motorState.equals("stop")) {
								DriveModel.setMotorState(0, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(1, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(2, RoverDriveModel.RELEASE);
								DriveModel.setMotorState(3, RoverDriveModel.RELEASE);
							} 
					    }
					}
				} 
				else {
					// digitalWrite(instructLED, LOW);
				}

				/*
				 * double volt1 = (analogRead(irPin1)); double distance1 =
				 * 8054.2 / ( pow ( volt1, .94 ) ); double volt2 =
				 * (analogRead(irPin2)); double distance2 = 8054.2 / ( pow
				 * (volt2, .94) );
				 * 
				 * if (distance1 < 50 || distance2 < 50) { tries = tries + 1; }
				 * else tries = 0;
				 * 
				 * if (tries > 1) { //sendSerial("s r n" distance1);
				 * motor1->run(RELEASE); motor2->run(RELEASE);
				 * motor3->run(RELEASE); motor4->run(RELEASE); delay(1000); }
				 */
			} catch (Exception e) {
				System.out.println("Error in Rover Run Code");
				e.printStackTrace();
			}
		}
	}

	private double getMotorSpeed(int power, double bat_level) {
		if (power == 0) {
			return 0;
		} else if (power > 0) {
			return Math.PI * 3;
		} else {
			return -3 * Math.PI;
		}
	}

	private double adjustForIncline(double speed, double incline) {
		return speed;
	}

	private void takePicture() {
		try {
			sendSerial("s r }");
			InputStream data = RoverCode.class.getResourceAsStream("/Rover Sample.jpg");
			delay(2000);
			sendSerial("s c [o]");
			delay(2000);
			int hold = data.read();
			int index;
			int x = 0;
			while (hold != -1) {
				index = 0;
				while (index < 60 && hold != -1) {
					Globals.writeToSerial((byte) hold, 'r');
					hold = data.read();
					index++;
					x++;
				}
				delay(2000);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean sendSerial(String mess) {
		char[] message = mess.toCharArray();
		if (!mute) {
			int x = 0;
			while (x < message.length) {
				if (message[x] == '\0') {
					break;
				}
				Globals.writeToSerial(message[x], 'r'); // Write to Serial one
														// char at a time
				try {
					Thread.sleep((int) (5 / Globals.getTimeScale())); // Pause
																		// for
																		// sending
				} catch (InterruptedException e) {
				}
				x++;
			}
			GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText() + mess
					+ "\t\t\t"
					+ (System.currentTimeMillis() - Globals.startTimeMillis)
					+ "\n");
			return true;
		} else {
			GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText()
					+ "Surpressed: " + mess + "\t\t"
					+ (System.currentTimeMillis() - Globals.startTimeMillis)
					+ "\n");
			return false;
		}
	}

	boolean sendSerial(float val) {
		return sendSerial(val + "");
	}

	boolean sendSerial(char mess) {
		if (!mute) {
			Globals.writeToSerial(mess, 'r');
			GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText() + mess
					+ "\t\t\t\t"
					+ (System.currentTimeMillis() - Globals.startTimeMillis)
					+ "\n");
			return true;
		} else {
			GUI.SerialHistoryLbl.setText(GUI.SerialHistoryLbl.getText()
					+ "Supressed: " + mess + "\t\t\t"
					+ (System.currentTimeMillis() - Globals.startTimeMillis)
					+ "\n");
			return false;
		}
	}

	private void delay(int length) {
		try {
			Thread.sleep((int) (length / Globals.getTimeScale()));
		} catch (Exception e) {
		}
	}

	private int strcmp(char[] first, String second) {
		try {
			char[] sec = second.toCharArray();
			int count = 0;
			int x = 0;
			while (first[x] != '\0') {
				if (first[x] != sec[x]) {
					return 1;
				}
				x++;
			}
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}
}
