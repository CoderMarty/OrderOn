package com.orderon.interfaces;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.ServerLog;

public interface IServer extends IAccess{
	
	public boolean syncOnServer(String hotelId, String sqlQueries);

	public ServerLog getLastServerLog(String hotelId);

	public JSONObject updateServerLog(String hotelId);

	public boolean updateServerStatus(String hotelId, Boolean updateServer);

	public boolean createServerLog(String hotelId);

}
