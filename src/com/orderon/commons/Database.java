package com.orderon;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class Database {
	private Boolean mAutoCommit;
	private Connection mConn;
	private String transactionLog = "";

	public interface OrderOnEntity {
		public void readFromDB(ResultSet rs);
	}
	
	public Database(Boolean transactionBased) {
		mAutoCommit = !transactionBased;
	}
	
	public Connection getConnection(String hotelId) throws Exception {
		try {
			String connectionURL = Configurator.getDBConnectionString() + hotelId + ".sqlite";
			Connection connection = null;
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(connectionURL);
			return connection;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Connection getConnection() throws Exception{
		return getConnection(Configurator.getHotelId());
	}
	
	private void openDB(String hotelId) {
		if(hotelId.equals("")) {
			hotelId = Configurator.getHotelId();
			if(hotelId.equals("")) {
				mConn = null;
				return;
			}
		}
		try {
			if (mAutoCommit) {
				Class.forName("org.sqlite.JDBC");
				
				String connectionString = Configurator.getDBConnectionString() + hotelId + ".sqlite";
				if(Configurator.getIsExtenstion()) {
					NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, Configurator.getExtensionUser(), Configurator.getExtensionPassword());
					SmbFile file = new SmbFile(connectionString, auth);
					connectionString = file.getPath();
				}
				
				mConn = DriverManager.getConnection(connectionString);
				mConn.setAutoCommit(mAutoCommit);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			mConn = null;
		}
	}
	
	private void closeDB() {
		try {
			if (mAutoCommit) {
				mConn.close();
				mConn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void beginTransaction(String hotelId) {
		if(hotelId.equals(""))
			hotelId = Configurator.getHotelId();
		try {
			if (mAutoCommit == false) {
				Class.forName("org.sqlite.JDBC");
				String connectionString = Configurator.getDBConnectionString() + hotelId + ".sqlite";
				mConn = DriverManager.getConnection(connectionString);
				mConn.setAutoCommit(mAutoCommit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void beginTransaction(){
		beginTransaction(Configurator.getHotelId());
	}

	public void commitTransaction() {
		try {
			if (!mAutoCommit) {
				mConn.commit();
				mConn.close();
				mConn = null;
				if(!Configurator.getIsServer()) {
					String previousContents = Configurator.readConfigFile(Configurator.getServerFile());
					Configurator.writeToServerFile(previousContents + transactionLog);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rollbackTransaction() {
		try {
			if (mAutoCommit == false) {
				mConn.rollback();
				mConn.close();
				mConn = null;
				transactionLog = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public <T extends OrderOnEntity> T getOneRecord(String sql, Class<T> ref, String hotelId) {
		Statement stmt = null;
		try {
			openDB(hotelId);
			stmt = mConn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				T entity = ref.newInstance();
				entity.readFromDB(rs);
				closeDB();
				return entity;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
		return null;
	}

	public Boolean hasRecords(String sql, String hotelId) {
		Statement stmt = null;
		try {
			openDB(hotelId);
			stmt = mConn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				closeDB();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeDB();
		return false;
	}

	public <T extends OrderOnEntity> ArrayList<T> getRecords(String sql,
			Class<T> ref, String hotelId) {
		Statement stmt = null;
		ArrayList<T> items = new ArrayList<T>();
		try {
			openDB(hotelId);
			stmt = mConn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				T entity = ref.newInstance();
				entity.readFromDB(rs);
				items.add(entity);
			}
			closeDB();
			return items;
		} catch (Exception e) {
			e.printStackTrace();
		}
		items.clear();
		closeDB();
		return items;
	}

	/*
	 * Use this method for SELECT query.
	 * Updates the database and also writes to the transaction log file.
	 * The transaction log file is a file that stores all the transaction that takes please on the app/management portal/kds.
	 * This file is used to update the database on the cloud periodically. 
	 * Once the database on cloud is updated, this contents of this file are deleted.
	 * 
	 * @param sql 			Stores the sql queries.
	 * @param hotelId 		Stores the hotel Id of the current hotel.
	 * @param writeToFile	True if needs this transaction be shown on cloud.
	 * 
	 * @return				True if Database is updated.
	 */
	public Boolean executeUpdate(String sql, String hotelId, boolean writeToFile) {
		Boolean ret = false;
		Statement stmt = null;
		openDB(hotelId);
		if(mConn == null)
			return false;
		try {
			stmt = mConn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeDB();
		if(ret && writeToFile && !Configurator.getIsServer()){
			sql = sql.trim();
			if(!sql.endsWith(";"))
				transactionLog += sql + ';';
			else
				transactionLog += sql;
			if(mAutoCommit) {
				String previousContents = Configurator.readConfigFile(Configurator.getServerFile());
				Configurator.writeToServerFile(previousContents + transactionLog);
				transactionLog = "";
			}
		}
		return ret;
	}

	/*
	 * Use this method for INSERT/UPDATE/DELETE queries.
	 * Updates the database and also writes to the transaction log file.
	 * The transaction log file is a file that stores all the transaction that takes please on the app/management portal/kds.
	 * This file is used to update the database on the cloud periodically. 
	 * Once the database on cloud is updated, this contents of this file are deleted.
	 * 
	 * @param sql 			Stores the sql queries.
	 * @param hotelId 		Stores the hotel Id of the current hotel.
	 * @param writeToFile	True if needs this transaction be shown on cloud.
	 * 
	 * @return				True if Database is updated.
	 */
	public Boolean executeUpdate(String sql, boolean writeToFile){
		return executeUpdate(sql, "", writeToFile);
	}
	
	public static String readRsString(ResultSet rs, String fieldName) {
		try {
			return rs.getString(fieldName)==null?"":rs.getString(fieldName);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static byte[] readRsBytes(ResultSet rs, String fieldName) {
		try {
			return rs.getBytes(fieldName);
		} catch (Exception e) {
			return null;
		}
		
	}

	public static Integer readRsInt(ResultSet rs, String fieldName) {
		try {
			return rs.getInt(fieldName);
		} catch (Exception e) {
			return 0;
		}
		
	}

	public static Boolean readRsBoolean(ResultSet rs, String fieldName) {
		try {
			return rs.getInt(fieldName) == 1;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static float readRsFloat(ResultSet rs, String fieldName) {
		try {
			return rs.getFloat(fieldName);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static BigDecimal readRsBigDecimal(ResultSet rs, String fieldName) {
		try {
			Double d = rs.getDouble(fieldName);
			String s = Double.toString(d);
			BigDecimal b = new BigDecimal(s);
			
			return b;
		} catch (Exception e) {
			return new BigDecimal("0.0");
		}
	}
	
	public static Double readRsDouble(ResultSet rs, String fieldName) {
		try {
			return rs.getDouble(fieldName);
		} catch (Exception e) {
			return 0.0;
		}
	}
	
	public static Date readRsDate(ResultSet rs, String fieldName) {
		try {
			return ((rs.getString(fieldName)==null)||rs.getString(fieldName).equals(""))?null:(new SimpleDateFormat("yyyy/MM/dd")).parse(rs
					.getString(fieldName));
		} catch (Exception e) {
			return null;
		}
	}
	
}