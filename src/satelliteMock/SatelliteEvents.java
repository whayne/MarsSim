package satelliteMock;

import objects.ThreadTimer;

public class SatelliteEvents {

	static SatelliteCode CODE = new SatelliteCode();
	
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
	
	static void BrowseForFile_Clicked(){
		CODE.browseForINOFile();
	}
}
