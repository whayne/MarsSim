package interfaceGUI;

import java.io.Serializable;

public class SaveFile implements Serializable{

	private static final long serialVersionUID = 1L;
	private String[][] commands;
	private String[][] tooltips;
	private String[][] icons;
	
	public SaveFile(String[][] cmds, String[][] tips, String[][] icons){
		commands = cmds;
		tooltips = tips;
		this.icons = icons;
	}
	
	public String[][] getCommands(){
		return commands;
	}
	
	public String[][] getTooltips(){
		return tooltips;
	}
	
	public String[][] getIcons(){
		return icons;
	}
}
