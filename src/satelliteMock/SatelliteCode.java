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
	
	String[][] variableNames = new String[15][0];
	Object[][] variableValues = new Object[15][0];
	Class[][] variableTypes = new Class[15][0];
	
	static void align(){
		GUI = SatelliteForm.frame;
	}
	
	// Reads in the file
	public void browseForINOFile(){
		JFileChooser browse = new JFileChooser();
		browse.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arduino Code", "ino"));
		browse.showOpenDialog(GUI);
		String filepath;
		try {
			filepath = browse.getSelectedFile().getAbsolutePath();
		}
		catch (Exception e){
			filepath = "";
		}
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
								variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name);
								variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], String.class);
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], storeVal);
								x = 0;
								while (x < storeVal.length()){
									variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name + "[" + x + "]");
									variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Character.class);
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], storeVal.charAt(x));
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
								variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name);
								variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], String.class);
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], "");
								prelength = ""; // recycling
								x = 0;
								while (x < length){
									variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name + "[" + x + "]");
									variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Character.class);
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\0');
									prelength += '\0';
									x++;
								}
								variableValues[bracketsInside][getIndexOf(variableNames[bracketsInside], name)] = prelength;
							}
						}
						else { // Is something else
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Object.class);
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value);
						}
					}
					else { // Is not an array
						variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name);
						if (type.equals("int")){
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Integer.class);
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], Integer.parseInt(value));
							}
							catch (Exception e){
								try {
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
								}
								catch (Exception i){
									if (value.equals("")){
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
									}
									else {
										// TODO code to handle math operations in initalization
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], 0);
									}
								}
							}
						}
						else if (type.equals("boolean")){
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Boolean.class);
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], Boolean.parseBoolean(value));
							}
							catch (Exception e){
								try {
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
								}
								catch (Exception i){
									if (value.equals("")){
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
									}
									else {
										// TODO code to analyze boolean, should exist later for if and while
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], false);
									}
								}
							}
						}
						else if (type.equals("char")){
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Character.class);
							try {
								if (value.length() == 3){
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value.charAt(1));
								}
								else {
									switch (value.charAt(2)){
									case '0':
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\0');
										break;
									case 'n':
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\n');
										break;
									case '\\':
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\\');
										break;				
									case 't':
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\t');
										break;						
									}
								}
							}
							catch (Exception e){
								try {
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
								}
								catch (Exception i){
									if (value.equals("Serial.read()")){
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], (char)Globals.ReadSerial('s'));
									}
									else {
										// I don't think there is anyother way to initalize this
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\0');
									}
								}
							}
						}
						else if (type.equals("File")){
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], File.class);
							if (value.equals("")){
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
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
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], filename);
								}
								else {
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(filename, bracketsInside));
								}
							}
						}
						else if (type.equals("float")){
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Float.class);
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], Float.parseFloat(value));
							}
							catch (Exception e){
								try {
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
								}
								catch (Exception i){
									if (value.equals("")){
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
									}
									else {
										// TODO code for more math operators
										variableValues[bracketsInside] = Augment(variableValues[bracketsInside], false);
									}
								}
							}
						}
						else { // Other object
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Object.class);
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value);
						}
					}
				}
				else if (contains(line, "=")){ // Variable value changed
					try {
						String value = getVariableValue(line);
						Class type = null;
						int locallity = bracketsInside;
						while (locallity >= 0){
							try {
								type = variableTypes[locallity][getIndexOf(variableNames[locallity], beginsWith)];
								break;
							}
							catch (Exception e) {}
							locallity--;
						}
						if (type.equals(Integer.class)){
							try {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = Integer.parseInt(value);
							}
							catch (Exception e){
								try {
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
								}
								catch (Exception i){
									// TODO code to handle math operations in initalization
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = 0;
								}
							}
						}
						else if (type.equals(Character.class)){
							try {
								if (value.length() == 3){
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = value.charAt(1);
								}
								else {
									switch (value.charAt(2)){
									case '0':
										variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\0';
										break;
									case 'n':
										variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\n';
										break;
									case '\\':
										variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\\';
										break;	
									case 't':
										variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\t';
										break;
									}
								}
							}
							catch (Exception e){
								e.printStackTrace();
								try {
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
								}
								catch (Exception ex){
									if (value.equals("Serial.read()")){
										variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = (char)Globals.ReadSerial('s');
									}
									else {
										// I don't think there is anyother way to initalize this
										variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\0';
									}
								}
							}
							if (contains(beginsWith, "[")){
								String strName = "";
								int x = 0;
								while (beginsWith.charAt(x) != '['){
									strName += beginsWith.charAt(x);
									x++;
								}
								String storeVal = "";
								int length = ((String)variableValues[locallity][getIndexOf(variableNames[locallity], strName)]).length();
								x = 0;
								while (x < length){
									storeVal += (char)getStoredValue(strName + "[" + x + "]", locallity);
									x++;
								}
								variableValues[locallity][getIndexOf(variableNames[locallity], strName)] = storeVal;
							}
						}
						else if (type.equals(Float.class)){
							variableTypes[locallity] = Augment(variableTypes[locallity], Float.class);
							try {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = Float.parseFloat(value);
							}
							catch (Exception e){
								try {
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
								}
								catch (Exception i){
									// TODO code for more math operators
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = 0.0;
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
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = filename;
							}
							else {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(filename, locallity);
							}
						}
						else if (type.equals(String.class)){
							if (getStartsWith(value).equals("new")){
								String string = "";
								int length = ((String)variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)]).length();
								int x = 0;
								while (x < length){
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith + "[" + x + "]")] = '\0';
									string += '\0';
									x++;
								}
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = string;
							}
						}
						else {
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = value;
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
		variableNames[1] = new String[0];
		variableTypes[1] = new Class[0];
		variableValues[1] = new Object[0];
		int bracketsInside = 0;
		
		int ifBracketClose = 0;
		boolean skipping = false;
		int whileLineStart = 0;
		int whileBracketClose = 0;;
		boolean looping = false;
		
		int i = 0;
		while (i < ino.length){
			i++;
			String line = ino[i-1];
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
			if (skipping){
				if (contains(line, "{")){
					bracketsInside++;
				}
				if (this.contains(line, "}")){
					bracketsInside--;
					if (bracketsInside == ifBracketClose){
						skipping = false;
					}
				}
				continue;
			}
			if (looping){
				System.out.println(line);
				if (contains(line,  "}")){
					if (bracketsInside - 1 == whileBracketClose){
						bracketsInside--;
						i = whileLineStart;
						looping = false;
						continue;
					}
				}
			}
			
			if (lineIsVariableDec(line)){ // A variable is declared
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
							variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name);
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], String.class);
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], storeVal);
							x = 0;
							while (x < storeVal.length()){
								variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name + "[" + x + "]");
								variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Character.class);
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], storeVal.charAt(x));
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
							variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name);
							variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], String.class);
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], "");
							prelength = ""; // recycling
							x = 0;
							while (x < length){
								variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name + "[" + x + "]");
								variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Character.class);
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\0');
								prelength += '\0';
								x++;
							}
							variableValues[bracketsInside][getIndexOf(variableNames[bracketsInside], name)] = prelength;
						}
					}
					else { // Is something else
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Object.class);
						variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value);
					}
				}
				else { // Is not an array
					variableNames[bracketsInside] = Augment(variableNames[bracketsInside], name);
					if (type.equals("int")){
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Integer.class);
						try {
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], Integer.parseInt(value));
						}
						catch (Exception e){
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
							}
							catch (Exception ex){
								if (value.equals("")){
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
								}
								else {
									// TODO code to handle math operations in initalization
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], 0);
								}
							}
						}
					}
					else if (type.equals("boolean")){
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Boolean.class);
						try {
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], Boolean.parseBoolean(value));
						}
						catch (Exception e){
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
							}
							catch (Exception ex){
								if (value.equals("")){
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
								}
								else {
									// TODO code to analyze boolean, should exist later for if and while
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], false);
								}
							}
						}
					}
					else if (type.equals("char")){
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Character.class);
						try {
							if (value.length() == 3){
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value.charAt(1));
							}
							else {
								switch (value.charAt(2)){
								case '0':
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\0');
									break;
								case 'n':
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\n');
									break;
								case '\\':
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\\');
									break;			
								case 't':
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\t');
									break;							
								}
							}
						}
						catch (Exception e){
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
							}
							catch (Exception ex){
								if (value.equals("Serial.read()")){
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], (char)Globals.ReadSerial('s'));
								}
								else {
									// I don't think there is anyother way to initalize this
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], '\0');
								}
							}
						}
					}
					else if (type.equals("File")){
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], File.class);
						if (value.equals("")){
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
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
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], filename);
							}
							else {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(filename, bracketsInside));
							}
						}
					}
					else if (type.equals("float")){
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Float.class);
						try {
							variableValues[bracketsInside] = Augment(variableValues[bracketsInside], Float.parseFloat(value));
						}
						catch (Exception e){
							try {
								variableValues[bracketsInside] = Augment(variableValues[bracketsInside], getStoredValue(value, bracketsInside));
							}
							catch (Exception ex){
								if (value.equals("")){
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], null);
								}
								else {
									// TODO code for more math operators
									variableValues[bracketsInside] = Augment(variableValues[bracketsInside], false);
								}
							}
						}
					}
					else if (type.equals("uint8_t")){
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Byte[].class);
						variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value);
					}
					else { // Other object
						variableTypes[bracketsInside] = Augment(variableTypes[bracketsInside], Object.class);
						variableValues[bracketsInside] = Augment(variableValues[bracketsInside], value);
					}
				}
			}
			else if (beginsWith.equals("if")){
				int x = 0;
				while (line.charAt(x) != '('){
					x++;
				}
				x++;
				String bool = "";
				int inside = 1;
				while (true){
					if (line.charAt(x) == '('){
						inside++;
					}
					if (line.charAt(x) == ')'){
						inside--;
						if (inside == 0){
							break;
						}
					}
					bool += line.charAt(x);
					x++;
				}
				boolean cont = evaluateBool(bool, bracketsInside);
				if (!cont){
					skipping = true;
					ifBracketClose = bracketsInside;
					if (contains(line, "{")){
						bracketsInside++;
					}
					continue;
				}
			}
			else if (beginsWith.equals("while")){
				int x = 0;
				while (line.charAt(x) != '('){
					x++;
				}
				x++;
				String bool = "";
				int inside = 1;
				while (true){
					if (line.charAt(x) == '('){
						inside++;
					}
					if (line.charAt(x) == ')'){
						inside--;
						if (inside == 0){
							break;
						}
					}
					bool += line.charAt(x);
					x++;
				}
				boolean cont = evaluateBool(bool, bracketsInside);
				if (!cont){ //if false looks like an if
					skipping = true;
					looping = false;
					ifBracketClose = bracketsInside;
					if (contains(line, "{")){
						bracketsInside++;
					}
					continue;
				}
				else {
					whileLineStart = i - 1;
					whileBracketClose = bracketsInside;
					if (contains(line, "{")){
						bracketsInside++;
					}
					looping = true;
					continue;
				}
			}
			else if (contains(line, "=")){
				try {
					String value = getVariableValue(line);
					Class type = null;
					int locallity = bracketsInside;
					while (locallity >= 0){
						try {
							type = variableTypes[locallity][getIndexOf(variableNames[locallity], beginsWith)];
							break;
						}
						catch (Exception e) {}
						locallity--;
					}
					if (type.equals(Integer.class)){
						try {
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = Integer.parseInt(value);
						}
						catch (Exception e){
							try {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
							}
							catch (Exception ex){
								// TODO code to handle math operations in initalization
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = 0;
							}
						}
					}
					else if (type.equals(Character.class)){
						try {
							if (value.length() == 3){
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = value.charAt(1);
							}
							else {
								switch (value.charAt(2)){
								case '0':
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\0';
									break;
								case 'n':
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\n';
									break;
								case '\\':
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\\';
									break;	
								case 't':
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\t';
									break;
								}
							}
						}
						catch (Exception e){
							e.printStackTrace();
							try {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
							}
							catch (Exception ex){
								if (value.equals("Serial.read()")){
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = (char)Globals.ReadSerial('s');
								}
								else {
									// I don't think there is anyother way to initalize this
									variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = '\0';
								}
							}
						}
						if (contains(beginsWith, "[")){
							String strName = "";
							int x = 0;
							while (beginsWith.charAt(x) != '['){
								strName += beginsWith.charAt(x);
								x++;
							}
							String storeVal = "";
							int length = ((String)variableValues[locallity][getIndexOf(variableNames[locallity], strName)]).length();
							x = 0;
							while (x < length){
								storeVal += (char)getStoredValue(strName + "[" + x + "]", locallity);
								x++;
							}
							variableValues[locallity][getIndexOf(variableNames[locallity], strName)] = storeVal;
						}
					}
					else if (type.equals(Boolean.class)){
						try {
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = Boolean.parseBoolean(value);
						}
						catch (Exception e){
							try {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
							}
							catch (Exception ex){
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = evaluateBool(value, locallity);
							}
						}
					}
					else if (type.equals(Float.class)){
						variableTypes[locallity] = Augment(variableTypes[locallity], Float.class);
						try {
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = Float.parseFloat(value);
						}
						catch (Exception e){
							try {
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(value, locallity);
							}
							catch (Exception ex){
								// TODO code for more math operators
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = 0.0;
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
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = filename;
						}
						else {
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = getStoredValue(filename, locallity);
						}
					}
					else if (type.equals(String.class)){
						if (getStartsWith(value).equals("new")){
							String string = "";
							int length = ((String)variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)]).length();
							int x = 0;
							while (x < length){
								variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith + "[" + x + "]")] = '\0';
								string += '\0';
								x++;
							}
							variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = string;
						}
					}
					else {
						variableValues[locallity][getIndexOf(variableNames[locallity], beginsWith)] = value;
					}
				} catch (Exception e) {} // If the 'variable' hasn't been declared
			}
			else if (beginsWith.equals("delay")){
				try {
					String prewait = "";
					int x = 0;
					while (line.charAt(x) != '('){
						x++;
					}
					x++;
					while (line.charAt(x) != ')'){
						prewait += line.charAt(x);
						x++;
					}
					Thread.sleep(Integer.parseInt(prewait));
				} catch (Exception e) {}
			}
			else if (contains(line, "++")){
				int x = bracketsInside; // Check variable Names from most local back
				while (x >= 0){
					try {
						variableValues[x][getIndexOf(variableNames[x], beginsWith)] = (int)variableValues[x][getIndexOf(variableNames[x], beginsWith)] + 1;
					}
					catch (Exception e){ }
					x--;
				}
			}
			else if (contains(line, "close()")){
				int z = bracketsInside;
				while (z >= 0){
					try { // If it is a file, close it by setting the filepath to null
						Class varType = variableTypes[z][getIndexOf(variableNames[z], beginsWith)];
						if (varType.equals(File.class)){
							variableValues[z][getIndexOf(variableNames[z], beginsWith)] = "";
						}
					} catch (Exception ex) {}
					z--;
				}
			}
			else if (beginsWith.equals("Serial")){
				String function = "";
				int x = 0;
				while (line.charAt(x) != '.'){
					x++;
				}
				x++;
				while (line.charAt(x) != '('){
					function += line.charAt(x);
					x++;
				}
				if(function.equals("read")){
					Globals.ReadSerial('s');
				}
				else {
					x++;
					String value = "";
					if (line.charAt(x) == '\"'){ //String
						x++;
						while (line.charAt(x) != '\"'){
							value += line.charAt(x);
							x++;
						}
						SerialPrint(value);
					}
					else { // Variable
						while (line.charAt(x) != ')'){
							value += line.charAt(x);
							x++;
						}
						Object data = getStoredValue(value, bracketsInside);
						try {
							SerialPrint((byte[])data);
						}
						catch (Exception e){
							SerialPrint(data + "");
						}
					}
				}
			}			
			if (contains(line, "{")){
				bracketsInside++;
			}
			if (contains(line, "}")){
				bracketsInside--;
				continue;
			}
		}		
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
		if (line.length() == 0){
			return "";
		}
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
		while (!isSymbol(chars[x]) || chars[x] == '[' || chars[x] == ']'){ //Last to for the identification of array variables
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
		if (begining.equals("const") || begining.equals("int") || begining.equals("char") || begining.equals("File") || begining.equals("byte") || begining.equals("uint16_t") || begining.equals("boolean") || begining.equals("uint8_t")){
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
	
	private Object getStoredValue(String name, int locallity){
		if (name.equals("Serial.read()")){
			return Globals.ReadSerial('s');
		}
		if (name.equals("Serial.available()")){
			return Globals.RFAvailable('s');
		}
		while (locallity >= 0){
			try {
				return variableValues[locallity][getIndexOf(variableNames[locallity], name)];
			}
			catch (Exception e){}
			locallity--;
		}
		return null;
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
	
	private void SerialPrint(byte[] data){
		int x = 0;
		while (x < data.length){
			Globals.writeToSerial(data[x], 's');
			try {
				Thread.sleep((int)(20 / Globals.getTimeScale()));
			} catch (Exception e) {}
		}
	}
	
	private boolean evaluateBool(String bool, int locallity){
		try {
		if (getStartsWith(bool).equals("strcmp")){
			// Get the 2 terms
			int x = 0;
			while (bool.charAt(x) != '('){
				x++;
			}
			x++;
			while (bool.charAt(x) == ' '){
				x++;
			}
			String first = "";
			while (bool.charAt(x) != ','){
				first += bool.charAt(x);
				x++;
			}
			x++;
			while (bool.charAt(x) == ' '){
				x++;
			}
			String second = "";
			while (bool.charAt(x) != ' ' && bool.charAt(x) != ')'){
				second += bool.charAt(x);
				x++;
			}
			//Get the term values
			if (first.charAt(0) == '\"'){//Is a string
				first = removeChar(first, '\"');
			}
			else {//Is a variable
				first = (String)getStoredValue(first, locallity);
			}
			if (second.charAt(0) == '\"'){
				second = removeChar(second, '\"');
			}
			else {
				second = (String)getStoredValue(second, locallity);
			}
			//Check
			return first.equals(second);
		}
		//else if (getStartsWith(bool).length() == bool.length()){//one term
		//	if (bool.equals("true")){
		//		return true;
		//	}
		//	else if (bool.equals("false")){
		//		return false;
		//	}
		//	else {
		//		return (boolean)getStoredValue(bool, locallity);
		//	}
		//}
		else if (contains(bool, "(") && !contains(bool, "()")){//must handle paraethesis first but not functions
			String[] bools = new String[0];
			String[] operations = new String[0];
			int x = 0;
			int y = 0;
			while (x < bool.length()){
				if (bool.charAt(x) == '('){
					bools = Augment(bools, "");
				}
				else if (bool.charAt(x) == ')'){
					operations = Augment(operations, "");
					y++;
				}
				else {
					try {
						bools[y] += bool.charAt(x);
					}
					catch (Exception e){
						if (bool.charAt(x) != ' '){
							operations[y - 1] += bool.charAt(x);
						}
					}
				}
				x++;
			}
			boolean[] values = new boolean[bools.length];
			x = 0;
			while (x < values.length){
				values[x] = evaluateBool(bools[x], locallity);
				x++;
			}
			boolean standing;
			switch (operations[0]){
			case "&&":
				standing = values[0] && values[1];
				break;
			case "||":
				standing = values[0] || values[1];
				break;
			default:
				standing = false;
				break;
			}
			x = 1;
			while (x < values.length - 1){
				switch (operations[x]){
				case "&&":
					standing = standing && values[x+1];
					break;
				case "||":
					standing = standing || values[x+1];
					break;
				default:
					standing = false;
					break;
				}
				x++;
			}
			return standing;
		}
		else if (contains(bool, "takePicutre()")){ //catches did picture take
			return false; //because I have no code to take a picture
		}
		else if (contains(bool, "available()") && !contains(bool, "Serial")){ //catches file.available()
			return true;
		}
		else {
			// get Terms
			int x= 0;
			String first = "";
			while (bool.charAt(x) != ' '){
				first += bool.charAt(x);
				x++;
			}
			x++;
			String operation = "";
			while (bool.charAt(x) != ' '){
				operation += bool.charAt(x);
				x++;
			}
			x++;
			String second = "";
			while (x < bool.length()){
				second += bool.charAt(x);
				x++;
			}
			//get Values
			Object firstval = null;
			Object secondval = null;
			Class compType = null;
			if (first.charAt(0) == '\''){//char
				if (bool.length() == 3){
					firstval = bool.charAt(1);
				}
				else {
					switch (bool.charAt(2)){
					case '0':
						firstval = '\0';
						break;
					case 'n':
						firstval = '\n';
						break;
					case '\\':
						firstval = '\\';
						break;			
					case 't':
						firstval = '\t';
						break;							
					}
				}
				compType = Character.class;
			}
			else if (47 < first.charAt(0) && first.charAt(0) < 58){//int
				try {
					firstval = Integer.parseInt(first);
					compType = Integer.class;
				}
				catch (Exception e){
					return false;
				}
			}
			else { //variable
				firstval = getStoredValue(first, locallity);
			}
			if (second.charAt(0) == '\''){//char
				if (second.length() == 3){
					secondval = second.charAt(1);
				}
				else {
					switch (second.charAt(2)){
					case '0':
						secondval = '\0';
						break;
					case 'n':
						secondval = '\n';
						break;
					case '\\':
						secondval = '\\';
						break;			
					case 't':
						secondval = '\t';
						break;							
					}
				}
				compType = Character.class;
			}
			else if (47 < second.charAt(0) && second.charAt(0) < 58){//int
				try {
					secondval = Integer.parseInt(second);
					compType = Integer.class;
				}
				catch (Exception e){
					return false;
				}
			}
			else { //variable
				secondval = getStoredValue(second, locallity);
			}
			//System.out.println(first + ":" + firstval + " " + operation + " " + second + ":" + secondval);
			try {
				if (compType == Character.class){
					try {
						return (char)firstval == (char)secondval;
					}
					catch (Exception e){
						try {
							return (char)(byte)firstval == (char)secondval;
						}
						catch (Exception ex){
							return (char)firstval == (char)(byte)secondval;
						}
					}
				}
				else {
					switch (operation){
					case "==":
						return (int)firstval == (int)secondval;
					case "<":
						return (int)firstval < (int)secondval;
					case ">":
						return (int)firstval > (int)secondval;
					case "<=":
						return (int)firstval <= (int)secondval;
					case ">=":
						return (int)firstval >= (int)secondval;
					}
				}
			}
			catch (Exception e){ //both variable - no type
				//TODO code to find variable type and compare
				e.printStackTrace();
			}
		}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
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
	
	private String removeChar(String str, char val){
		String out = "";
		int x = 0;
		while (x < str.length()){
			if (str.charAt(x) != val){
				out += str.charAt(x);
			}
			x++;
		}
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
