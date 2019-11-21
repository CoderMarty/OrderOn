package com.orderon.commons;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.DBTransaction;
import com.orderon.dao.ServerManager;
import com.orderon.interfaces.IServer;

public class Database {
	private Boolean mAutoCommit;
	private Connection mConn;
	private StringBuilder transactionLog;

	public interface OrderOnEntity {
		public void readFromDB(ResultSet rs);
	}
	
	public Database(Boolean transactionBased) {
		mAutoCommit = !transactionBased;
		transactionLog = new StringBuilder();
	}
	
	public Connection getConnection(String outletId) throws Exception {
		try {
			String connectionURL = Configurator.getDBConnectionString() + outletId + ".sqlite";
			Connection connection = null;
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(connectionURL);
			return connection;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void openDB(String outletId) {
		if(outletId.equals("")) {
			mConn = null;
			return;
		}
		try {
			if (mAutoCommit) {
				Class.forName("org.sqlite.JDBC");
				String connectionString = Configurator.getDBConnectionString() + outletId + ".sqlite";
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
			if (mAutoCommit && mConn != null) {
				mConn.close();
				mConn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void beginTransaction(String outletId) {
		try {
			if(outletId.equals("")) {
				mConn = null;
				return;
			}
			if (!mAutoCommit) {
				Class.forName("org.sqlite.JDBC");
				String connectionString = Configurator.getDBConnectionString() + outletId + ".sqlite";
				mConn = DriverManager.getConnection(connectionString);
				mConn.setAutoCommit(mAutoCommit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commitTransaction(String outletId, boolean isAServerUpdate) {
		try {
			if(outletId.equals("")) {
				mConn = null;
				return;
			}
			if(mConn == null) {
				return;
			}
			if (!mAutoCommit) {
				mConn.commit();
				mConn.close();
				mConn = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mAutoCommit) {
				return;
			}
			//If syncing on server
			if(isAServerUpdate) {
				return;
			}
			loadTransactionsToDB(outletId);
		}
	}

	public void rollbackTransaction() {
		try {
			if(mConn == null) {
				return;
			}
			if (!mAutoCommit) {
				mConn.rollback();
				mConn.close();
				mConn = null;
				transactionLog = new StringBuilder();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public <T extends OrderOnEntity> T getOneRecord(String sql, Class<T> ref, String outletId) {
		Statement stmt = null;
		try {
			openDB(outletId);
			stmt = mConn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				T entity = ref.newInstance();
				entity.readFromDB(rs);
				closeDB();
				return entity;
			}
		} catch (Exception e) {
			System.out.println("Outlet Id:" + outletId);
			e.printStackTrace();
		} finally {
			closeDB();
		}
		
		return null;
	}

	public Boolean hasRecords(String sql, String outletId) {
		Statement stmt = null;
		try {
			openDB(outletId);
			stmt = mConn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				closeDB();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return false;
	}

	public <T extends OrderOnEntity> ArrayList<T> getRecords(String sql,
			Class<T> ref, String outletId) {
		Statement stmt = null;
		ArrayList<T> items = new ArrayList<T>();
		try {
			openDB(outletId);
			stmt = mConn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				T entity = ref.newInstance();
				entity.readFromDB(rs);
				items.add(entity);
			}
		} catch (NullPointerException e) {
			System.out.println(sql);
			System.out.println("No Record Found");
		}catch (Exception e) {
			System.out.println("Outlet Id:" + outletId);
			e.printStackTrace();
		} finally {
			closeDB();
		}
		return items;
	}

	public JSONArray getJsonDBRecords(String sql, String outletId) {
		Statement stmt = null;
		JSONArray items = new JSONArray();
		try {
			openDB(outletId);
			stmt = mConn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				DBTransaction entity = DBTransaction.class.newInstance();
				entity.readFromDB(rs);
				items.put(entity.getTransaction());
			}
		} catch (NullPointerException e) {
			System.out.println(sql);
			System.out.println("No Record Found");
		}catch (Exception e) {
			System.out.println("Outlet Id:" + outletId);
			e.printStackTrace();
		} finally {
			closeDB();
		}
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
	 * @param outletId 		Stores the hotel Id of the current hotel.
	 * @param writeToFile	True if needs this transaction be shown on cloud.
	 * 
	 * @return				True if Database is updated.
	 */
	public Boolean executeUpdate(String sql, String outletId, boolean writeToFile) {
		Boolean ret = false;
		Statement stmt = null;
		openDB(outletId);
		if(mConn == null)
			return false;
		try {
			stmt = mConn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			ret = true;
		} catch (Exception e) {
			System.out.println(sql);
			e.printStackTrace();
		} finally {
			closeDB();
			if(ret && writeToFile){
				sql = sql.trim();
				if(sql.isEmpty()) {
					return ret;
				}
				transactionLog.append(sql);
				if(!sql.endsWith(";"))
					transactionLog.append(";");
				if(mAutoCommit) {
					loadTransactionsToDB(outletId);
				}
			}
		}
		return ret;
	}
	
	private void loadTransactionsToDB(String outletId) {
		
		if(mConn != null) {
			return;
		}
		if(Configurator.getIsServer() || Configurator.getIsDebug()) {
			return;
		}
		IServer dao = new ServerManager(false);
		if(transactionLog.toString().isEmpty()) {
			return;
		}
		dao.addTransaction(outletId, transactionLog.toString());
		transactionLog = new StringBuilder();
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

	public static long readRsLong(ResultSet rs, String fieldName) {
		try {
			return rs.getLong(fieldName);
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
