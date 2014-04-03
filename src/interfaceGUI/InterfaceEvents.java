package interfaceGUI;

public class InterfaceEvents {

	static InterfaceCode CODE = new InterfaceCode();
	
	static void Window_Opened(){
		CODE.initalize();
		CODE.resetConnection();
		simulatorWrapper.WrapperEvents.WindowOpened();
	}
	
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
		//CODE.writeToLog(note);
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
	
	static void AddInstructionClicked(){
		CODE.AddInstruction();
	}
	
	static void SubmitInstructionClicked(){
		CODE.SendInstructions();
	}
	
	static void InstructionModClicked(int which){
		CODE.editInstructionSendList(which);
	}
	
	static void RoverListChanged(){
		CODE.RoverCommandChanged();
	}
	
	static void SatelliteListChanged(){
		CODE.SalelliteCommandChanged();
	}
	
	static void ParametersListChanged(){
		CODE.ParametersChanged();
	}
	
	static void InstructionListChanged(){
		if (CODE.GUI.InstructionsList.getSelectedIndex() != -1){
			CODE.GUI.InstructionsDeleteBtn.setEnabled(true);
			CODE.GUI.InstructionsEditBtn.setEnabled(true);
			CODE.GUI.InstructionsUpBtn.setEnabled(CODE.GUI.InstructionsList.getSelectedIndex() != 0);
			CODE.GUI.InstructionsDownBtn.setEnabled(CODE.GUI.InstructionsList.getSelectedIndex() != CODE.GUI.InstructionsList.getItems().length-1);
		}
		else {
			CODE.GUI.InstructionsDeleteBtn.setEnabled(false);
			CODE.GUI.InstructionsEditBtn.setEnabled(false);
			CODE.GUI.InstructionsUpBtn.setEnabled(false);
			CODE.GUI.InstructionsDownBtn.setEnabled(false);
		}
	}
	
	public static void InstructionEditorFinish(boolean addRover, boolean addSat, String title, InstructionObj[] instruct){
		CODE.addInstructionsToList2(addRover, addSat, title, instruct);
	}
	
}
