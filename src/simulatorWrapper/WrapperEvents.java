package simulatorWrapper;

public class WrapperEvents {

	static WrapperMain CODE = new WrapperMain();
	
	static void Window_Opened(){
		CODE.startSimulator();
	}
	
}
