package com.orderon.dao;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IServer;

public class ServerManager extends AccessManager implements IServer{

	public ServerManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean syncOnServer(String hotelId, String sqlQueries) {

		if (!db.executeUpdate(sqlQueries, hotelId, false)) {
			return false;
		}
		System.out.println("All Transaction logged Successfully at " + hotelId + ".");
		return true;
	}

	@Override
	public boolean syncOnServer(String hotelId, JSONArray sqlQueries) {
		try {
			for (int i = 0; i < sqlQueries.length(); i++) {
				if (!db.executeUpdate(sqlQueries.getString(i), hotelId, false)) {
					return false;
				}	
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("All Transaction logged Successfully at " + hotelId + "." + " Time Stamp: " + LocalDateTime.now());
		return true;
	}

	@Override
	public ServerLog getLastServerLog(String hotelId) {

		String sql = "SELECT * FROM ServerLog WHERE hotelId = '" + escapeString(hotelId)
				+ "' Order by id desc Limit 1;";

		return db.getOneRecord(sql, ServerLog.class, hotelId);
	}

	@Override
	public JSONObject updateServerLog(String hotelId) {

		JSONObject outObj = new JSONObject();
		LocalDateTime now = LocalDateTime.now();
		String sql = "UPDATE ServerLog SET lastUpdateTime = '" + now + "', status = 1 WHERE hotelId = '"
				+ hotelId + "';";

		try {
			outObj.put("status", db.executeUpdate(sql, hotelId, false));
			outObj.put("time", now.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean updateServerStatus(String hotelId, Boolean updateServer) {

		String sql = "UPDATE ServerLog SET status = 0 WHERE hotelId = '" + hotelId + "';";

		return db.executeUpdate(sql, hotelId, updateServer);
	}

	@Override
	public boolean createServerLog(String hotelId) {

		String sql = "INSERT into ServerLog ('hotelId', 'lastUpdateTime', 'status') VALUES ('" + escapeString(hotelId)
				+ "','" + LocalDateTime.now() + "', 1);";

		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public boolean addTransaction(String hotelId, String transaction) {
		
		String sql = "INSERT into DBTransactions ('transactions') VALUES ('" + escapeString(transaction) + "');";

		return db.executeUpdate(sql, hotelId, false);
	}

	@Override
	public JSONArray getAllTransactions(String outletId) {
		
		String sql = "SELECT * FROM DBTransactions;";
		
		return db.getJsonDBRecords(sql, outletId);
	}

	@Override
	public boolean deleteAllTransactions(String hotelId) {

		String sql = "DELETE FROM DBTransactions;";

		return db.executeUpdate(sql, hotelId, false);
	}

}
