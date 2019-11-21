package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.ServerLog;

public interface IServer extends IAccess{
	
	public boolean syncOnServer(String outletId, JSONArray sqlQueries);
	
	public boolean syncOnServer(String outletId, String sqlQueries);
	
	public boolean checkIfSyncActive(String outletId);

	public ServerLog getActiveServerLog(String outletId);

	public boolean addServerLog(String outletId);
	
	public JSONObject markLogInActive(String outletId, int batchId);

	public boolean markLogSuccessful(String outletId, int batchId);

	public boolean markLogDeleted(String outletId, int batchId);
	
	public boolean markLogComplete(String outletId, int batchId);

	public ArrayList<ServerLog> getUndeletedBatches(String outletId);

	public ArrayList<ServerLog> getUnloggedBatches(String outletId);
	
	public boolean addTransaction(String outletId, String transaction);
	
	public boolean assignBatchIdToTransactions(String outletId, int batchId);
	
	public JSONArray getAllTransactionsInBatch(String outletId, int batchId);
	
	public JSONArray getUnBatchedTransactions(String outletId);
	
	public boolean deleteAllTransactionsInBatch(String outletId, int batchId);
}
