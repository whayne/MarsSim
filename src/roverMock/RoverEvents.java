package roverMock;

import objects.ThreadTimer;

public class RoverEvents {

	static RoverCode CODE = new RoverCode();
	
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
}
