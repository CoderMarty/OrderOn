package com.orderon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	
	public static boolean getIsExtenstion() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return Boolean.valueOf(configObj.getString("isExtension"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getExtensionUser() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("extensionUser");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getExtensionPassword() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		try {
			configObj = new JSONObject(config);
			return configObj.getString("extensionPassword");
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
	
	public static OrderPrinter getPrinters() {
		String config = readConfigFile(getConfigFile());
		JSONObject configObj;
		OrderPrinter printer = new OrderPrinter();
		try {
			configObj = new JSONObject(config);
			printer.setBar(configObj.getString("bar"));
			printer.setBeverage(configObj.getString("beverage"));
			printer.setCashier(configObj.getString("cashier"));
			printer.setKitchen(configObj.getString("kitchen"));
			printer.setOutDoor(configObj.getString("outdoor"));
			printer.setSummary(configObj.getString("summary"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return printer;
	}
	
	public static class OrderPrinter{
		private String bar;
		private String beverage;
		private String cashier;
		private String kitchen;
		private String outDoor;
		private String summary;
		
		public String getBar() {
			return bar;
		}
		public String getBeverage() {
			return beverage;
		}
		public String getCashier() {
			return cashier;
		}
		public String getKitchen() {
			return kitchen;
		}
		public String getOutDoor() {
			return outDoor;
		}
		public String getSummary() {
			return summary;
		}
		public void setBar(String bar) {
			this.bar = bar;
		}
		public void setBeverage(String beverage) {
			this.beverage = beverage;
		}
		public void setCashier(String cashier) {
			this.cashier = cashier;
		}
		public void setKitchen(String kitchen) {
			this.kitchen = kitchen;
		}
		public void setOutDoor(String outDoor) {
			this.outDoor = outDoor;
		}
		public void setSummary(String summary) {
			this.summary = summary;
		}
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
