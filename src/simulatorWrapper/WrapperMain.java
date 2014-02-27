package simulatorWrapper;

public class WrapperMain {

	static WrapperForm GUI = new WrapperForm();
	
	static void align(){
		GUI = WrapperForm.frame;
	}
	
	public void startSimulator(){
		
		interfaceGUI.Form.main(new String[1]);
		
	}
	
	
}
