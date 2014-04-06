package roverMock;

import objects.ThreadTimer;

public class RoverEvents {

	public static RoverCode CODE = new RoverCode();
	
	static void Window_Opened(){
		simulatorWrapper.WrapperEvents.WindowOpened();
	}
	
	public static void StartCode(){
		new ThreadTimer(0, new Runnable(){
			public void run(){
				CODE.runCode();
			}
		}, 1);
	}
	
	public static void setGUIVisible(boolean b){
		CODE.GUI.setVisible(b);
	}
}
