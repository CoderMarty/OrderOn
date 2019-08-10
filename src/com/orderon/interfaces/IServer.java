package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.DBTransaction;
import com.orderon.dao.AccessManager.ServerLog;

public interface IServer extends IAccess{
	
	public boolean syncOnServer(String hotelId, String sqlQueries);

	public ServerLog getLastServerLog(String hotelId);

	public JSONObject updateServerLog(String hotelId);

	public boolean updateServerStatus(String hotelId, Boolean updateServer);

	public boolean createServerLog(String hotelId);
	
	public boolean addTransaction(String hotelId, String transaction);
	
	public ArrayList<DBTransaction> getAllTransactions(String hotelId);
	
	public boolean deleteAllTransactions(String hotelId);

}
