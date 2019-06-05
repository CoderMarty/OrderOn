package com.orderon.commons;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class Configurator {
	public static String readConfigFile(String filename) {
        StringBuilder sb = new StringBuilder();
		try {
		    BufferedReader br = new BufferedReader(new FileReader(filename));
		    try {
		        String line = br.readLine();
	
		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        return sb.toString();
		    } finally {
		        br.close();
		    }
		}
		catch (Exception e){
			sb.append("{}");
		}
		return sb.toString();
	}
	
	public static void writeToServerFile(String content) {
		FileWriter fw = null;
		try{
			fw = new FileWriter(getServerFile());
			fw.write(content);

		}catch(IOException e) {
			System.out.println("Server File Writer Caused IOException.");
			e.printStackTrace();
		}finally {
			try{
				if(fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static String getConfigFile() {
		return System.getenv("ORDERON_CONFIG");
	}
	
	public static String getServerFile() {
		return System.getenv("ORDERON_SERVER");
	}
	
	public static String getDBConnectionString() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("DBConnectionString");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getOutletId() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("hotelId");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getTransactionLogLocation() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("transactionLog");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "/";
	}
	
	public static String getBillSize() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("billSize");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "100";
	}
	
	public static String getBillFont() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("billFont");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Default";
	}
	
	public static String getIp() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("ip");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "localhost";
	}
	
	public static String getDownloadLocation() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("downloadLocation");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static Boolean getIsServer() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return Boolean.valueOf(configObj.getString("isServer"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Boolean getIsDebug() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return Boolean.valueOf(configObj.getString("isDebug"));
		} catch (JSONException e) {
			return false;
		}
	}
	
	public static String getTransactionLog() {
		return readConfigFile(getServerFile());
	}
	
	public static String getTomcatLocation() {
		return System.getenv("CATALINA_HOME");
	}
}
