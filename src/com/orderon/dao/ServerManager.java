package com.orderon.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
	public boolean syncOnServer(String outletId, String sqlQueries) {

		if (!db.executeUpdate(sqlQueries, outletId, false)) {
			return false;
		}
		System.out.println("All Transaction logged Successfully at " + outletId + ".");
		return true;
	}

	@Override
	public boolean syncOnServer(String outletId, JSONArray sqlQueries) {
		try {
			for (int i = 0; i < sqlQueries.length(); i++) {
				if(sqlQueries.length()==0) {
					continue;
				}
				if (!db.executeUpdate(sqlQueries.getString(i), outletId, false)) {
					return false;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("All Transaction logged Successfully at " + outletId + "." + " Time Stamp: " + LocalDateTime.now());
		return true;
	}
	
	@Override
	public boolean checkIfSyncActive(String outletId) {
		
		String sql = "SELECT * FROM ServerLog WHERE outletId = '"+outletId+"' AND isActive = 'true';";
		
		return db.hasRecords(sql, outletId);
	}

	@Override
	public ServerLog getActiveServerLog(String outletId) {

		String sql = "SELECT * FROM ServerLog WHERE outletId = '" + escapeString(outletId)
				+ "' AND isActive = 'true' Order by id desc Limit 1;";

		return db.getOneRecord(sql, ServerLog.class, outletId);
	}

	@Override
	public boolean addServerLog(String outletId) {

		String sql = "INSERT INTO ServerLog (outletId) VALUES ('"+outletId+"');";

		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public JSONObject markLogInActive(String outletId, int batchId) {

		JSONObject outObj = new JSONObject();
		LocalDateTime now = LocalDateTime.now();
		String sql = "UPDATE ServerLog SET updateTime = '" + now + "', isActive = 'false' WHERE outletId = '"
				+ outletId + "' AND id = "+batchId+";";

		try {
			outObj.put("status", db.executeUpdate(sql, outletId, false));
			outObj.put("time", now.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean markLogSuccessful(String outletId, int batchId) {

		String sql = "UPDATE ServerLog SET isTransactionLogged = 'true' WHERE outletId = '" + outletId + "' AND id = "+batchId+";";

		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean markLogDeleted(String outletId, int batchId) {

		String sql = "UPDATE ServerLog SET isDataDeleted = 'true' WHERE outletId = '" + outletId + "' AND id = "+batchId+";";

		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean markLogComplete(String outletId, int batchId) {
		
		LocalDateTime now = LocalDateTime.now();
		String sql = "UPDATE ServerLog SET isTransactionLogged = 'true', "
				+ "isDataDeleted = 'true', "
				+ "updateTime = '" + now + "', "
				+ "isActive = 'false' "
				+ "WHERE outletId = '" + outletId + "' AND id = "+batchId+";";

		return db.executeUpdate(sql, outletId, false);
	}
	
	@Override
	public ArrayList<ServerLog> getUndeletedBatches(String outletId) {
		
		String sql = "SELECT * FROM ServerLog WHERE isTransactionLogged == 'true' AND isDataDeleted = 'false';";
		
		return db.getRecords(sql, ServerLog.class, outletId);
	}

	@Override
	public ArrayList<ServerLog> getUnloggedBatches(String outletId) {

		String sql = "SELECT * FROM ServerLog WHERE isTransactionLogged == 'false';";
		
		return db.getRecords(sql, ServerLog.class, outletId);
	}
	
	@Override
	public boolean addTransaction(String outletId, String transaction) {
		
		String sql = "INSERT into DBTransactions ('transactions') VALUES ('" + escapeString(transaction) + "');";

		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public boolean assignBatchIdToTransactions(String outletId, int batchId) {
		
		String sql = "UPDATE DBTransactions SET batchId = "+batchId+" WHERE batchId is null;";

		return db.executeUpdate(sql, outletId, false);
	}

	@Override
	public JSONArray getAllTransactionsInBatch(String outletId, int batchId) {
		
		String sql = "SELECT * FROM DBTransactions WHERE batchId = "+batchId+";";
		
		return db.getJsonDBRecords(sql, outletId);
	}

	@Override
	public JSONArray getUnBatchedTransactions(String outletId) {
		
		String sql = "SELECT * FROM DBTransactions WHERE batchId is null;";
		
		return db.getJsonDBRecords(sql, outletId);
	}

	@Override
	public boolean deleteAllTransactionsInBatch(String outletId, int batchId) {

		String sql = "DELETE FROM DBTransactions WHERE batchId = "+batchId+";";

		return db.executeUpdate(sql, outletId, false);
	}
}
