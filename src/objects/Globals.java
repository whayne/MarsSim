package objects;

import simulatorWrapper.WrapperEvents;

public class Globals {

	private static byte[] SatelliteRFserial = new byte[0];
	private static byte[] RoverRFserial = new byte[0];
	private static byte[] GroundRFserial = new byte[0];
	
	private static double timeScale = 1.0;
	
	public static void writeToSerial(char write, char from){
		writeToSerial((byte)write, from);
	}
	
	public static void writeToSerial(byte write, char from){
		if (from == 'r'){
			if (SatelliteRFserial.length < 64){ // Serial buffer is only 64 bytes
				SatelliteRFserial = Augment(SatelliteRFserial, write);
			}
			if (GroundRFserial.length < 64){
				GroundRFserial = Augment(GroundRFserial, write);
			}
		}
		else if (from == 's'){
			if (RoverRFserial.length < 64){
				RoverRFserial = Augment(RoverRFserial, write);
			}
			if (GroundRFserial.length < 64){
				GroundRFserial = Augment(GroundRFserial, write);
			}
		}
		else if (from == 'g'){
			if (SatelliteRFserial.length < 64){
				SatelliteRFserial = Augment(SatelliteRFserial, write);
			}
			if (RoverRFserial.length < 64){
				RoverRFserial = Augment(RoverRFserial, write);
			}
		}
		WrapperEvents.SerialBuffersChanged(RoverRFserial, SatelliteRFserial, GroundRFserial);
	}
	
	public static int RFAvailable(char which){ // Returns the number of chars waiting
		if (which == 'r'){
			return RoverRFserial.length;
		}
		else if (which == 's'){
			return SatelliteRFserial.length;
		}
		else if (which == 'g'){
			return GroundRFserial.length;
		}
		return -1;
	}
	
	public static byte ReadSerial(char which){ // Returns the first waiting character
		byte out = '\0';
		if (RFAvailable(which) > 0){
			if (which == 'r'){
				out = RoverRFserial[0];
				RoverRFserial = dropFirst(RoverRFserial);
			}
			else if (which == 's'){
				out = SatelliteRFserial[0];
				SatelliteRFserial = dropFirst(SatelliteRFserial);
			}
			else if (which == 'g'){
				out = GroundRFserial[0];
				GroundRFserial = dropFirst(GroundRFserial);
			}
		}
		WrapperEvents.SerialBuffersChanged(RoverRFserial, SatelliteRFserial, GroundRFserial);
		return out;
	}
	
	public static byte PeekSerial(char which){  // get first waiting character without changing availability
		byte out = '\0';
		if (RFAvailable(which) > 0){
			if (which == 'r'){
				out = RoverRFserial[0];
			}
			else if (which == 's'){
				out = SatelliteRFserial[0];
			}
			else if (which == 'g'){
				out = GroundRFserial[0];
			}
		}
		return out;
	}
	
	private static byte[] Augment(byte[] array, byte val){
		byte[] out = new byte[array.length + 1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private static byte[] dropFirst(byte[] val){
		byte[] out = new byte[val.length - 1];
		int x = 0;
		while (x < out.length){
			out[x] = val[x + 1];
			x++;
		}
		return out;
	}
	
	public static double getTimeScale(){
		return timeScale;
	}
	
	public static void setTimeScale(double time){
		timeScale = time;
	}
	
}
