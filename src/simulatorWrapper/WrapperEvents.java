package simulatorWrapper;

public class WrapperEvents {

	static WrapperMain CODE = new WrapperMain();
	
	static void Window_Opened(){
		CODE.startSimulator();
	}
	
	public static void SerialBuffersChanged(byte[] rover, byte[] sat, byte[] ground){
		CODE.updateBufferLabels(rover, sat, ground);
	}
	
}
