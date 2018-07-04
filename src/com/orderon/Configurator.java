package com.orderon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
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
		BufferedWriter bw = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter(getServerFile());
			bw = new BufferedWriter(fw);
			bw.write(content);

		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try{
				if(bw != null)
					bw.close();
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
	
	public static String getHotelId() {
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

	public static int getMinOrders() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getInt("minOrdersPerDay");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getImagesLocation() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try { 
			configObj = new JSONObject(config);
			return configObj.getString("imagesLocation");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "/";
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
	
	public static int getKOTWidth() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getInt("KOTWidth");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 700;
	}
	
	public static int getKOTHeight() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getInt("KOTHeight");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 800;
	}
	
	public static Double getDivisor() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getDouble("divisor");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0.142;
	}
	public static String getBeveragePrinter() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("Beverage");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Cashier";
	}
	public static String getKitchenPrinter() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("Kitchen");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Cashier";
	}
	public static String getBarPrinter() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("Bar");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Cashier";
	}
	public static String getOutdoorPrinter() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("Outdoor");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Cashier";
	}
	
	public static String getKOTFont() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("font");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "Arial";
	}
	
	public static String getTransactionLog() {
		return readConfigFile(getServerFile());
	}

	public static JSONArray getCollections() {
		String config = readConfigFile(getConfigFile());
		
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getJSONArray("collections");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
}
