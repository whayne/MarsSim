package simulatorWrapper;

import objects.Globals;
import objects.ThreadTimer;

public class WrapperMain {

	static WrapperForm GUI = new WrapperForm();
	static MapFrame MAP;
	
	// Start Up
	
	static void align(){
		GUI = WrapperForm.frame;
	}
	
	static void alignMap(){
		MAP = MapFrame.frame;
	}
	
	public void startSimulator(){
		
		new ThreadTimer(0, new Runnable() {
			public void run(){
				interfaceGUI.InterfaceForm.main(new String[1]); // Start the interface GUI
			}
		}, 1);
		
		new ThreadTimer(0, new Runnable() {
			public void run(){
				roverMock.RoverForm.main(new String[1]); // Start the mock Rover
			}
		}, 1);
		
		new ThreadTimer(0, new Runnable() {
			public void run(){
				satelliteMock.SatelliteForm.main(new String[1]); // Start the mock Satellite
			}
		}, 1);
		
		new ThreadTimer(0, new Runnable() {
			public void run(){
				MAP = new MapFrame();
				MapFrame.main(new String[1]);
			}
		}, 1);
			
	}
	
	
	// Monitoring
	
	public void updateBufferLabels(byte[] rov, byte[] sat, byte[] ground){
		char[] input = new char[rov.length];
		int x = 0;
		while (x < input.length){
			input[x] = (char)rov[x];
			x++;
		}
		GUI.RoverBufferLbl.setText( buildString(input) );
		input = new char[sat.length];
		x = 0;
		while (x < input.length){
			input[x] = (char)sat[x];
			x++;
		}
		GUI.SatelliteBufferLbl.setText( buildString(input) );
		input = new char[ground.length];
		x = 0;
		while (x < input.length){
			input[x] = (char)ground[x];
			x++;
		}
		GUI.GroundBufferLbl.setText( buildString(input) );
		GUI.RoverAvailableLbl.setText(rov.length + "");
		GUI.SatelliteAvailableLbl.setText(sat.length + "");
		GUI.GroundAvailableLbl.setText(ground.length + "");
	}
	
	public void updateTimeScale(){
		Globals.setTimeScale(Math.pow(2, (GUI.TimeSlider.getValue() - 30) / 10.0));
	}
	
	
	// Support Methods
	
	private String buildString(char[] array){
		String out = "";
		int x = 0;
		while (x < array.length){
			out += array[x];
			x++;
		}
		return out;
	}
	
}
