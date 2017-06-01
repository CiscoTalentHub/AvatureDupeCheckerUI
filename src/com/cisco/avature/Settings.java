package com.cisco.avature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Settings {
	
	private static JSONObject settingsObj = new JSONObject();
	//private static FileWriter file ;
	private String settingName;
	private String password;
	private String username;
	private String dir;
	
	
	
	public Settings() throws Exception {
		settingName = "src/settings.txt";
		settingsObj = (JSONObject) parseSettings();
		//settingsObj = new JSONObject();
		//file = new FileWriter(settingName);
		
		
	}
	
	public static void setUsernamePass(String user, String pass) throws FileNotFoundException, IOException, ParseException{
		
		settingsObj.put("User",  user);
		settingsObj.put("Pass",  pass);
	
		writeSettingsFile(settingsObj);
		
		/**
		
		if(!settingsFileExists()){
			settingsObj.put("User",  user);
			settingsObj.put("Pass",  pass);
			settingsObj.put("Directory", "src/");
			
			writeSettingsFile(settingsObj);
			
		} else {
			
			settingsObj = (JSONObject) parseSettings();
			
	        settingsObj.put("User",  user);
			settingsObj.put("Pass",  pass);
		
			writeSettingsFile(settingsObj);
		}

	*/
	}
	
	public static void setDirectory(String dir) throws FileNotFoundException, IOException, ParseException {

		settingsObj = (JSONObject) parseSettings();
		
		settingsObj.put("Directory",  dir);
		
	
		writeSettingsFile(settingsObj);
		
		/**
		
		if(!settingsFileExists()){
			settingsObj.put("User",  "");
			settingsObj.put("Pass",  "");
			settingsObj.put("Directory", dir);
			
			writeSettingsFile(settingsObj);
			
		} else {
			
				 
			settingsObj = (JSONObject) parseSettings();
			
			settingsObj.put("Directory",  dir);
			
		
			writeSettingsFile(settingsObj);
		}
		
		*/
	}
	
	public String getUsername() {
		
		username = (String) settingsObj.get("User");
		return username;
	}
	
	public String getPassword() {
		
		//settingsObj = (JSONObject) parseSettings();
		password = (String) settingsObj.get("Pass");
		return password;
		
	}
	
	public String getDirectory() {
		
		//settingsObj = (JSONObject) parseSettings();
		dir = (String) settingsObj.get("Directory");
		return dir;
	}
	
	/**
	
	private static boolean settingsFileExists() {
		
		File settingsFile = new File("src/settings.txt");
		boolean exists = settingsFile.exists();
		
		return exists;
		
	}
	*/
	
	private static Object parseSettings() throws FileNotFoundException, IOException, ParseException {
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(
                    "src/settings.txt"));
		
		return obj;
		
		
	}
	
	private static void writeSettingsFile(JSONObject settings) {
		
		try (FileWriter file = new FileWriter("src/settings.txt")) {
			file.write(settings.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
