package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONObject;

import com.orderon.dao.AccessManager.User;

public interface IUser extends IAccess{
	
	public Boolean userExists(String hotelId, String userId);

	public JSONObject addUser(String hotelId, String userId, String employeeId, int userType, String userPasswd);
	
	public JSONObject addUser(User systemUser, String hotelId, String userId, String employeeId, int userType, String userPasswd);

	public JSONObject updateUser(User systemUser, String hotelId, String userId, String oldPassword, String password, int userType);

	public User getUserById(String hotelId, String userId);
	
	public User getUser(String hotelId, String userId);

	public User getUserByEmpId(String hotelId, String employeeId);

	public ArrayList<User> getAllUsers(String hotelId);

	public JSONObject deleteUser(User systemUser, String hotelId, User selectedUser);
	
	public boolean checkUserAccess(User systemUser, int userType);
}
