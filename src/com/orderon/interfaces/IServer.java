package com.orderon.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.ServerLog;

public interface IServer extends IAccess{
	
	public boolean syncOnServer(String hotelId, String sqlQueries);
	
	public boolean syncOnServer(String hotelId, JSONArray sqlQueries);

	public ServerLog getLastServerLog(String hotelId);

	public JSONObject updateServerLog(String hotelId);

	public boolean updateServerStatus(String hotelId, Boolean updateServer);

	public boolean createServerLog(String hotelId);
	
	public boolean addTransaction(String hotelId, String transaction);
	
	public JSONArray getAllTransactions(String hotelId);
	
	public boolean deleteAllTransactions(String hotelId);

}
