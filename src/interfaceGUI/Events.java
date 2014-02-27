package interfaceGUI;

public class Events {

	static Code CODE = new Code();
	
	static void COMPortChanged(){
		CODE.changeCOMPort();
	}
	
	static void windowClosing(){
		CODE.closeProgram();
	}
	
	static void actionButtonClicked(int section, int which){
		CODE.ActionButtonClicked(section, which);
	}
	
	static void addNoteToLog(String note){
		CODE.writeToLog(note);
	}
	
	static void sendMsg(String msg){
		CODE.writeToSerial(msg);
	}
	
	static void sendRoverMsg(){
		CODE.sendRoverCommand();
	}
	
	static void sendSatMessage(){
		CODE.sendSatCommand();
	}
	
	static void roverLinkClicked(int which){
		switch (which){
		case 0:
			CODE.addRoverBtn();
			break;
		case 1:
			CODE.editRoverBtn1();
			break;
		case 2:
			CODE.deleteRoverBtn1();
			break;
		}
	}
	
	static void satLinkClicked(int which){
		switch (which){
		case 0:
			CODE.addSatBtn();
			break;
		case 1:
			CODE.editSatBtn1();
			break;
		case 2:
			CODE.deleteSatBtn1();
			break;
		}
	}
	
}
