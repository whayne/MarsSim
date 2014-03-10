package satelliteMock;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import objects.Globals;
import objects.ThreadTimer;

public class SatelliteCode {

	static SatelliteForm GUI = new SatelliteForm();
	private String[] CodeFile = new String[0];
	private boolean hasCode = false;
	
	String[] variableNames = new String[0];
	Object[] variableValues = new Object[0];
	Class[] variableTypes = new Class[0];
	
	String[] localVariableNames = new String[0];
	Object[] localVariableValues = new Object[0];
	Class[] localVariableTypes = new Class[0];
	
	static void align(){
		GUI = SatelliteForm.frame;
	}
	
	// Reads in the file
	public void browseForINOFile(){
		JFileChooser browse = new JFileChooser();
		browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arduino Code", "ino"));
		browse.showOpenDialog(GUI);
		String filepath = browse.getSelectedFile().getAbsolutePath();
		GUI.FileLocationTxt.setText(filepath);
		newFileConnected();
	}
	
	//figures out the global variables and defines the loop function
	public void newFileConnected(){
		if (GUI.FileLocationTxt.getText().equals("")){
			return;
		}
		try {
			Scanner file = new Scanner(new File(GUI.FileLocationTxt.getText()));
			int bracketsInside = 0;
			
			while (file.hasNextLine()){
				String line = file.nextLine();
				if (line.length() < 1){
					continue; 
				}
				String beginsWith = getStartsWith(line);
				if (beginsWith.equals("") || beginsWith.equals("//") || beginsWith.equals("#") || beginsWith.equals("digitalWrite")){
					continue;
				}
				if (beginsWith.equals("{")){
					bracketsInside++;
					continue;
				}
				
				if (lineIsVariableDec(line)){ // A variable is declared
					int y = variableNames.length;
					String type = getVariableType(line);
					String name = getVariableName(line);
					String value = getVariableValue(line);
					if (contains(type, "*")){ // Is an array
						if (getStartsWith(type).equals("char")){ //Is a String
							int x = 0;
							if (contains(value, "\"")){
								while (value.charAt(x) != '\"'){
									x++;
								}
								x++;
								String storeVal = "";
								while (value.charAt(x) != '\"'){
									storeVal += value.charAt(x);
									x++;
								}
								variableNames = Augment(variableNames, name);
								variableTypes = Augment(variableTypes, String.class);
								variableValues = Augment(variableValues, storeVal);
								x = 0;
								while (x < storeVal.length()){
									variableNames = Augment(variableNames, name + "[" + x + "]");
									variableTypes = Augment(variableTypes, Character.class);
									variableValues = Augment(variableValues, storeVal.charAt(x));
									x++;
								}
							}
							else {
								String prelength = "";
								while (value.charAt(x) != '['){
									x++;
								}
								x++;
								while (value.charAt(x) != ']'){
									prelength += value.charAt(x);
									x++;
								}
								int length = Integer.parseInt(prelength);
								variableNames = Augment(variableNames, name);
								variableTypes = Augment(variableTypes, String.class);
								variableValues = Augment(variableValues, "");
								prelength = ""; // recycling
								x = 0;
								while (x < length){
									variableNames = Augment(variableNames, name + "[" + x + "]");
									variableTypes = Augment(variableTypes, Character.class);
									variableValues = Augment(variableValues, '\0');
									prelength += '\0';
									x++;
								}
								variableValues[getIndexOf(variableNames, name)] = prelength;
							}
						}
						else { // Is something else
							variableTypes = Augment(variableTypes, Object.class);
							variableValues = Augment(variableValues, value);
						}
					}
					else { // Is not an array
						variableNames = Augment(variableNames, name);
						if (type.equals("int")){
							variableTypes = Augment(variableTypes, Integer.class);
							try {
								variableValues = Augment(variableValues, Integer.parseInt(value));
							}
							catch (Exception e){
								try {
									variableValues = Augment(variableValues, getStoredValue(value));
								}
								catch (Exception i){
									if (value.equals("")){
										variableValues = Augment(variableValues, null);
									}
									else {
										// TODO code to handle math operations in initalization
										variableValues = Augment(variableValues, 0);
									}
								}
							}
						}
						else if (type.equals("boolean")){
							variableTypes = Augment(variableTypes, Boolean.class);
							try {
								variableValues = Augment(variableValues, Boolean.parseBoolean(value));
							}
							catch (Exception e){
								try {
									variableValues = Augment(variableValues, getStoredValue(value));
								}
								catch (Exception i){
									if (value.equals("")){
										variableValues = Augment(variableValues, null);
									}
									else {
										// TODO code to analyze boolean, should exist later for if and while
										variableValues = Augment(variableValues, false);
									}
								}
							}
						}
						else if (type.equals("char")){
							variableTypes = Augment(variableTypes, Character.class);
							try {
								if (value.length() == 3){
									variableValues = Augment(variableValues, value.charAt(1));
								}
								else {
									switch (value.charAt(3)){
									case '0':
										variableValues = Augment(variableValues, '\0');
										break;
									case 'n':
										variableValues = Augment(variableValues, '\n');
										break;
									case '\\':
										variableValues = Augment(variableValues, '\\');
										break;									
									}
								}
							}
							catch (Exception e){
								try {
									variableValues = Augment(variableValues, getStoredValue(value));
								}
								catch (Exception i){
									if (value.equals("Serial.read()")){
										variableValues = Augment(variableValues, (char)Globals.ReadSerial('s'));
									}
									else {
										// I don't think there is anyother way to initalize this
										variableValues = Augment(variableValues, '\0');
									}
								}
							}
						}
						else if (type.equals("File")){
							variableTypes = Augment(variableTypes, File.class);
							if (value.equals("")){
								variableValues = Augment(variableValues, null);
							}
							else {
								String filename = "";
								boolean string = false;
								int x = 0;
								while (value.charAt(x) != '(') {
									x++;
								}
								x++;
								if (value.charAt(x) == '\"'){
									string = true;
									x++;
								}
								while (value.charAt(x) != '\"' || value.charAt(x) == ')'){
									filename += value.charAt(x);
									x++;
								}
								if (string){
									variableValues = Augment(variableValues, filename);
								}
								else {
									variableValues = Augment(variableValues, getStoredValue(filename));
								}
							}
						}
						else if (type.equals("float")){
							variableTypes = Augment(variableTypes, Float.class);
							try {
								variableValues = Augment(variableValues, Float.parseFloat(value));
							}
							catch (Exception e){
								try {
									variableValues = Augment(variableValues, getStoredValue(value));
								}
								catch (Exception i){
									if (value.equals("")){
										variableValues = Augment(variableValues, null);
									}
									else {
										// TODO code for more math operators
										variableValues = Augment(variableValues, false);
									}
								}
							}
						}
						else { // Other object
							variableTypes = Augment(variableTypes, Object.class);
							variableValues = Augment(variableValues, value);
						}
					}
				}
				else if (contains(line, "=")){ // Variable value changed
					try {
						String value = getVariableValue(line);
						Class type = variableTypes[getIndexOf(variableNames, getVariableName(line))];
						if (type.equals(Integer.class)){
							try {
								variableValues[getIndexOf(variableNames, getVariableName(line))] = Integer.parseInt(value);
							}
							catch (Exception e){
								try {
									variableValues[getIndexOf(variableNames, getVariableName(line))] = getStoredValue(value);
								}
								catch (Exception i){
									// TODO code to handle math operations in initalization
									variableValues[getIndexOf(variableNames, getVariableName(line))] = 0;
								}
							}
						}
						else if (type.equals(Character.class)){
							try {
								if (value.length() == 3){
									variableValues = Augment(variableValues, value.charAt(1));
								}
								else {
									switch (value.charAt(3)){
									case '0':
										variableValues[getIndexOf(variableNames, getVariableName(line))] = '\0';
										break;
									case 'n':
										variableValues[getIndexOf(variableNames, getVariableName(line))] = '\n';
										break;
									case '\\':
										variableValues[getIndexOf(variableNames, getVariableName(line))] = '\\';
										break;									
									}
								}
							}
							catch (Exception e){
								try {
									variableValues[getIndexOf(variableNames, getVariableName(line))] = getStoredValue(value);
								}
								catch (Exception i){
									if (value.equals("Serial.read()")){
										variableValues[getIndexOf(variableNames, getVariableName(line))] = (char)Globals.ReadSerial('s');
									}
									else {
										// I don't think there is anyother way to initalize this
										variableValues[getIndexOf(variableNames, getVariableName(line))] = '\0';
									}
								}
							}
							if (contains(value, "[")){
								String strName = getStartsWith(value);
								String storeVal = "";
								int length = ((String)variableValues[getIndexOf(variableNames, getVariableName(strName))]).length();
								int x = 0;
								while (x < length){
									storeVal += (char)getStoredValue(strName);
									x++;
								}
								variableValues[getIndexOf(variableNames, getVariableName(strName))] = storeVal;
							}
						}
						else if (type.equals(Boolean.class)){
							try {
								variableValues[getIndexOf(variableNames, getVariableName(line))] = Boolean.parseBoolean(value);
							}
							catch (Exception e){
								try {
									variableValues[getIndexOf(variableNames, getVariableName(line))] = getStoredValue(value);
								}
								catch (Exception i){
									// TODO code to analyze boolean, should exist later for if and while
									variableValues[getIndexOf(variableNames, getVariableName(line))] = false;
								}
							}
						}
						else if (type.equals(Float.class)){
							variableTypes = Augment(variableTypes, Float.class);
							try {
								variableValues[getIndexOf(variableNames, getVariableName(line))] = Float.parseFloat(value);
							}
							catch (Exception e){
								try {
									variableValues[getIndexOf(variableNames, getVariableName(line))] = getStoredValue(value);
								}
								catch (Exception i){
									// TODO code for more math operators
									variableValues[getIndexOf(variableNames, getVariableName(line))] = 0.0;
								}
							}
						}
						else if (type.equals(File.class)){
							String filename = "";
							boolean string = false;
							int x = 0;
							while (value.charAt(x) != '(') {
								x++;
							}
							x++;
							if (value.charAt(x) == '\"'){
								string = true;
								x++;
							}
							while (value.charAt(x) != '\"' || value.charAt(x) == ')'){
								filename += value.charAt(x);
								x++;
							}
							if (string){
								variableValues[getIndexOf(variableNames, getVariableName(line))] = filename;
							}
							else {
								variableValues[getIndexOf(variableNames, getVariableName(line))] = getStoredValue(filename);
							}
						}
						else if (type.equals(String.class)){
							if (getStartsWith(value).equals("new")){
								String string = "";
								int length = ((String)variableValues[getIndexOf(variableNames, getVariableName(line))]).length();
								int x = 0;
								while (x < length){
									variableValues[getIndexOf(variableNames, getVariableName(line) + "[" + x + "]")] = '\0';
									string += '\0';
									x++;
								}
								variableValues[getIndexOf(variableNames, getVariableName(line))] = string;
							}
						}
						else {
							variableValues[getIndexOf(variableNames, getVariableName(line))] = value;
						}
					} catch (Exception e) {} // If the 'variable' hasn't been declared
				}
				
				if (contains(line, "void loop()")){ // Creates the loop function for interpretFile
					while (!contains(line, "{")) {
						line = file.nextLine();
					}
					bracketsInside = 1;
					while (bracketsInside > 0){
						line = file.nextLine();
						if (contains(line, "{")){
							bracketsInside++;
						}
						if (contains(line, "}")){
							bracketsInside--;
						}
						CodeFile = Augment(CodeFile, line);
					}
					break;
				}				
				if (contains(line, "{")){
					bracketsInside++;
				}
				if (contains(line, "}")){
					bracketsInside--;
					continue;
				}			
			}
			hasCode = true;
		} 
		catch (Exception e) {
			e.printStackTrace();
			//JOptionPane.showConfirmDialog(GUI, "The File Could Not be found.", "Invalid File", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			CodeFile = null;
			hasCode = false;
		}		
	}
	
	// Runs the loop method from the file
	private void interpretFile(String[] ino){
		if (!hasCode){
			return;
		}
		localVariableNames = new String[0];
		localVariableTypes = new Class[0];
		localVariableValues = new Object[0];
		int bracketsInside = 0;
		
		int i = -1;
		while (i < ino.length){
			i++;
			String line = ino[i];
			if (line.length() < 1){
				continue; 
			}
			String beginsWith = getStartsWith(line);
			if (beginsWith.equals("") || beginsWith.equals("//") || beginsWith.equals("#") || beginsWith.equals("digitalWrite")){
				continue;
			}
			if (beginsWith.equals("{")){
				bracketsInside++;
				continue;
			}
			
			if (lineIsVariableDec(line)){ // A variable is declared
				variableNames = Augment(variableNames, getVariableName(line));
				//variableTypes = Augment(variableTypes, getVariableType(line));
				variableValues = Augment(variableValues, getVariableValue(line));
				int y = variableNames.length - 1;
			}
			else if (beginsWith.equals("if")){
				
			}
			else if (beginsWith.equals("while")){
				
			}
			else if (contains(line, "=")){ // Variable value changed
				try {
					variableValues[getIndexOf(variableNames, getStartsWith(line))] = getVariableValue(line);
				} catch (Exception e) {} // If the 'variable' hasn't been declared
			}
			else if (beginsWith.equals("delay")){
				
			}
			
			
			if (contains(line, "{")){
				bracketsInside++;
			}
			if (contains(line, "}")){
				bracketsInside--;
				continue;
			}
		}
		
		hasCode = false;
		
	}
	
	public void runCode(){
		while (true){
			interpretFile(CodeFile);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {}
		}
	}
	
	
	// CODE FOR TRANSLATING INO CODE
	
	private boolean contains(String str, String val){
		boolean inString = false;
		char[] search = str.toCharArray();
		char[] vals = val.toCharArray();
		int x = 0;
		while (x < search.length - vals.length + 1){
			if (inString){
				if (search[x] == '\"'){
					inString = false;
				}
				x++;
				continue;
			}
			if (search[x] == '/' && search[x+1] == '/'){
				return false;
			}
			if (search[x] == '\"'){
				inString = true;
				//x++;  Allows " to besearched for
				//continue;
			}
			if (search[x] == vals[0]){
				int matches = 1;
				int y = 1;
				while (y < vals.length){
					if (search[x + y] == vals[y]){
						matches++;
					}
					y++;
				}
				if (matches == vals.length){
					return true;
				}
			}
			x++;
		}
		return false;
	}
	
	private String getStartsWith(String line){
		String out = "";
		char[] chars = line.toCharArray();
		int x = 0;
		while (chars[x] == ' ' || chars[x] == '\t'){
			x++;
			if (x == chars.length){
				return "";
			}
		}
		if (chars[x] == '/' && chars[x+1] == '/'){
			return "//";
		}
		if (isSymbol(chars[x])){
			return chars[x] + "";
		}
		while (!isSymbol(chars[x])){
			out += chars[x];
			x++;
		}
		return out;
	}
	
	private boolean isSymbol(char val){
		return (val < 48) || (val > 57 && val < 65) || (val > 90 && val < 97) || (val > 122);
	}
	
	private boolean lineIsVariableDec(String line){
		String begining = getStartsWith(line);
		if (begining.equals("const") || begining.equals("int") || begining.equals("char") || begining.equals("File") || begining.equals("byte") || begining.equals("uint16_t") || begining.equals("boolean")){
			return true;
		}
		if (contains(line, "= " + begining)){
			return true;
		}
		return false;
	}
	
	private String getVariableType(String line){
		String begining = getStartsWith(line);
		char[] working = line.toCharArray();
		if (begining.equals("const")){
			int y = 0;
			while (working[y] != 'c'){ y++; }
			working[y] = ' ';
			working[y+1] = ' ';
			working[y+2] = ' ';
			working[y+3] = ' ';
			working[y+4] = ' ';
			begining = getStartsWith(buildString(working));
		}
		int x = 0;
		while (working[x] == ' ' || working[x] == '\t'){ x++; }
		while (working[x] != ' '){ x++;	}
		if (working[x-1] == '*'){
			return begining + "*";
		}
		return begining;
	}
	
	private String getVariableName(String line){
		String begining = getStartsWith(line);
		char[] working = line.toCharArray();
		if (begining.equals("const")){
			int y = 0;
			while (working[y] != 'c'){ y++; }
			working[y] = ' ';
			working[y+1] = ' ';
			working[y+2] = ' ';
			working[y+3] = ' ';
			working[y+4] = ' ';
			begining = getStartsWith(buildString(working));
		}
		int x = 0;
		while (working[x] == ' ' || working[x] == '\t'){ x++; }
		while (working[x] != ' '){ x++; }
		x++;
		String name = "";
		while (working[x] != ' ' && working[x] != ';'){
			name += working[x];
			x++;
		}
		return name;
	}
	
	private String getVariableValue(String line){
		if (!contains(line, "=")){
			return "";
		}
		char[] working = line.toCharArray();
		int x = 0;
		while (working[x] != '='){ x++;	}
		x += 2;
		String val = "";
		while (working[x] != ';'){
			val += working[x];
			x++;
		}
		return val;
	}
	
	private Object getStoredValue(String name){
		try {
			return localVariableValues[getIndexOf(localVariableNames, name)];
		}
		catch (Exception e){
			try {
				return variableValues[getIndexOf(variableNames, name)];
			}
			catch (Exception i){
				return null;
			}
		}
	}
	
	// CODE FOR EMULATING FUNCTIONS
	
	private void SerialPrint(String msg){
		char[] output = msg.toCharArray();
		int x = 0;
		while (x < output.length){
			Globals.writeToSerial(output[x], 's'); // Write to Serial one char at a time
			try {
				Thread.sleep((int)(20 / Globals.getTimeScale())); // Pause for sending
			} catch (InterruptedException e) {}
			x++;
		}
	}
	
	private void SerialPrint(char[] msg){
		String message = "";
		int x = 0;
		while (x < msg.length){
			if (msg[x] == '\0'){
				break;
			}
			message += msg[x];
			x++;
		}
		SerialPrint(message);
	}
	
	
	// SUPPORT FUNCTIONS
	
	private String[] Augment(String[] array, String val){
		String[] out = new String[array.length+1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private Object[] Augment(Object[] array, Object val){
		Object[] out = new Object[array.length +1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private Class[] Augment(Class[] array, Class val){
		Class[] out = new Class[array.length +1];
		int x = 0;
		while (x < array.length){
			out[x] = array[x];
			x++;
		}
		out[x] = val;
		return out;
	}
	
	private String buildString(char[] array){
		String out = "";
		int x = 0;
		while (x < array.length){
			if (array[x] != '\0'){
				out += array[x];
			}
			x++;
		}
		return out;
	}
	
	private int getIndexOf(String[] array, String val){
		int x = 0;
		while (x < array.length){
			if (val.equals(array[x])){
				return x;
			}
			x++;
		}
		return -1;
	}
	
}
