package simulatorWrapper;

public class WrapperMain {

	static WrapperForm GUI = new WrapperForm();
	
	
	// Start Up
	
	static void align(){
		GUI = WrapperForm.frame;
	}
	
	public void startSimulator(){
		
		interfaceGUI.InterfaceForm.main(new String[1]); // Start the interface GUI
		
		roverMock.RoverForm.main(new String[1]); // Start the mock Rover
		
		satelliteMock.SatelliteForm.main(new String[1]); // Start the mock Satellite
			
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
