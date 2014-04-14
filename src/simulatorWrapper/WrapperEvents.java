package simulatorWrapper;

import objects.DecimalPoint;

public class WrapperEvents {

	static WrapperMain CODE = new WrapperMain();
	
	static void Window_Opened(){
		CODE.startSimulator();
	}
	
	public static void SerialBuffersChanged(byte[] rover, byte[] sat, byte[] ground){
		CODE.updateBufferLabels(rover, sat, ground);
	}
	
	private static int windows = 0;
	
	public static void WindowOpened(){
		windows++;
		if (windows == 3){
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
			satelliteMock.SatelliteEvents.StartCode();
			roverMock.RoverEvents.StartCode();
		}
	}
	
	static void TimeSliderValue_Changed(){
		CODE.updateTimeScale();
	}
	
	public static void moveRover(double distance, double angle){
		CODE.MAP.setRoverDirection(CODE.MAP.getRoverDirection() + angle);
		CODE.MAP.setRoverLocation(CODE.MAP.getRoverLocation().offset(distance*Math.cos(CODE.MAP.getRoverDirection()), distance*(Math.sin(CODE.MAP.getRoverDirection()))));
	}
	
	public static DecimalPoint getRoverLocation(){
		return CODE.MAP.getRoverLocation();
	}
	
	public static double getRoverDirection(){
		return CODE.MAP.getRoverDirection();
	}
	
	public static void setRoverConditions(DecimalPoint location, double angle){
		CODE.MAP.setRoverLocation(location);
		CODE.MAP.setRoverDirection(angle);
	}
	
	public static double getIncline(){
		return CODE.MAP.getIncline();
	}
	
}
