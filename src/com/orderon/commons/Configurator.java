package com.orderon.commons;

import java.io.BufferedReader;
import java.io.FileReader;

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
	
	private static String getConfigFile() {
		return System.getenv("ORDERON_CONFIG_V4");
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
	
	public static String getSystemId() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("systemId");
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
	public static String getTomcatLocation() {
		return System.getenv("CATALINA_HOME");
	}
}
