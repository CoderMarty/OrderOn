package com.orderon.interfaces;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.User;

public interface IUserAuthentication extends IAccess{
	public User validUser(String outletId, String userId, String password);

	public User validateUser(String outletId, String userId, String password);

	public Boolean validOnlinePlatform(String outletId, String userId, String password);

	public User validateAccess1(String outletId, String userId, String password);

	public User validateAdmin(String outletId, String userId, String password);

	public User validateOwner(String outletId, String userId, String password);

	public User validateSecretUser(String outletId, String userId, String password);

	public User validKDSUser(String outletId, String userId, String password);

	public boolean removeToken(String outletId, String userId);

	public JSONObject validateToken(String outletId, String userId, String authToken);
}
