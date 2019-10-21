package com.orderon.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.commons.EncryptDecryptString;
import com.orderon.commons.UserType;
import com.orderon.interfaces.IUser;
import com.orderon.interfaces.IUserAuthentication;

public class UserManager extends AccessManager implements IUser, IUserAuthentication {

	public UserManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean userExists(String hotelId, String userId) {
		User user = getUserById(hotelId, userId);
		if (user != null) {
			return true;
		}
		return false;
	}

	@Override
	public JSONObject addUser(String hotelId, String userId, String employeeId, int userType, String userpassword) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			EncryptDecryptString eds = new EncryptDecryptString();
			byte[] salt = eds.getNextSalt();
			byte[] hash = eds.hash(userpassword.toCharArray(), salt);
			
			String sql = "INSERT INTO Users ('hotelId', 'userId', 'userPasswd', 'employeeId', 'userType', 'authToken', 'timeStamp', 'salt') VALUES ('"
					+ escapeString(hotelId) + "','" + escapeString(userId) + "',?,'"
					+ escapeString(employeeId) + "'," + Integer.toString(userType) + ",NULL,NULL, ? );";
	
			Connection conn;
			conn = db.getConnection(hotelId);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBytes(1, hash);
			pstmt.setBytes(2, salt);
			if(pstmt.executeUpdate() > 0) {
				outObj.put("status", true);
			}else {
				outObj.put("message", "Could not update User. Please contact support");
				return outObj;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject addUser(User systemUser, String hotelId, String userId, String employeeId, int userType, String userpassword) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			if(!this.checkUserAccess(systemUser, userType)) {
				outObj.put("message", "UNAUTHORISED to Add User of the Selected Designation.");
				return outObj;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.addUser(hotelId, userId, employeeId, userType, userpassword);
	}

	@Override
	public JSONObject updateUser(User systemUser, String hotelId, String userId, String oldPassword, String password, int userType) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			if(!systemUser.getUserId().equals(userId)) {
				if(!this.checkUserAccess(systemUser, userType)) {
					outObj.put("message", "UNAUTHORISED to Update/Edit user of Selected Designation.");
					return outObj;
				}
			}
			
			EncryptDecryptString eds = new EncryptDecryptString();
			
			if(this.validUser(hotelId, userId, oldPassword)==null) {
				outObj.put("message", "Old Passwords don't match. Please try again.");
				return outObj;
			}
			
			byte[] salt = eds.getNextSalt();
			byte[] hash = eds.hash(password.toCharArray(), salt);
			
			String sql = "UPDATE Users SET userType= " + Integer.toString(userType) + ", userPasswd = ?, salt = ? "
					+ " WHERE userId='" + escapeString(userId) + "' AND hotelId='" + hotelId + "';";
	
			Connection conn;
			conn = db.getConnection(hotelId);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBytes(1, hash);
			pstmt.setBytes(2, salt);
			if(pstmt.executeUpdate() > 0) {
				outObj.put("status", true);
			}else {
				outObj.put("message", "Could not update User. Please contact support");
				return outObj;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}
	
	@Override
	public boolean checkUserAccess(User systemUser, int userType) {
		
		UserType systemUserType = UserType.getType(systemUser.getUserType());
		UserType newUserType = UserType.getType(userType);
		
		if(systemUserType.getUserLevel()>=newUserType.getUserLevel()) {
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<User> getAllCaptains(String hotelId) {
		String sql = "SELECT userId FROM Users WHERE userType == 9 ORDER BY userId;";
		return db.getRecords(sql, User.class, hotelId);
	}

	@Override
	public User getUserById(String hotelId, String userId) {
		String sql = "SELECT Users.*, (Employee.firstName || ' ' || Employee.surName) AS name "
				+ "FROM Users, Employee WHERE userId='" + escapeString(userId) + "' AND Users.hotelId='"
				+ escapeString(hotelId) + "' AND Users.employeeId = Employee.employeeId;";
		return db.getOneRecord(sql, User.class, hotelId);
	}

	@Override
	public User getUser(String hotelId, String userId) {
		String sql = "SELECT * FROM Users WHERE userId='" + escapeString(userId) + "' AND hotelId='"+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, User.class, hotelId);
	}

	@Override
	public User getUserByEmpId(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Users WHERE employeeId='" + escapeString(employeeId) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, User.class, hotelId);
	}

	@Override
	public ArrayList<User> getAllUsers(String hotelId) {
		String sql = "SELECT Users.*, (Employee.firstName || ' ' || Employee.surName) AS name " + 
				"FROM Users, Employee WHERE Users.hotelId='" + escapeString(hotelId) + "' AND Users.employeeId == Employee.employeeId;";
		return db.getRecords(sql, User.class, hotelId);
	}

	@Override
	public JSONObject deleteUser(User systemUser, String hotelId, User selectedUser) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			if(!this.checkUserAccess(systemUser, selectedUser.getUserType())) {
				outObj.put("message", "UNAUTHORISED to Add User of the Selected Designation.");
				return outObj;
			}
			String sql = "DELETE FROM Users WHERE userId = '" + selectedUser.getUserId() + "' AND hotelId='" + hotelId + "';";
			outObj.put("status", db.executeUpdate(sql, hotelId, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}
	
	@Override
	public User validUser(String hotelId, String userId, String password) {

		User user = this.getUser(hotelId, userId);
		if(user == null) {
			return null;
		}
		EncryptDecryptString eds = new EncryptDecryptString();
		
		if(eds.isExpectedPassword(password.toCharArray(), user.getSalt(), user.getPassword())) {
			return user;
		}
		return null;
	}

	private void setAuthToken(String userId, String hotelId) {

		String sql = "UPDATE Users SET authToken = ABS(RANDOM() % 10000000000), timeStamp = '"
				+ LocalDateTime.now().toString() + "' WHERE userId = '" + userId + "'AND hotelId = '" + hotelId + "';";
		db.executeUpdate(sql, hotelId, false);
	}

	@Override
	public User validateUser(String hotelId, String userId, String password) {

		setAuthToken(userId, hotelId);
		User user = this.validUser(hotelId, userId, password);
		if (user!=null) {
			return user;
		}
		String sql = "UPDATE Users SET authToken = 0, timeStamp = NULL WHERE userId = '" + userId + "'AND hotelId = '"
			+ hotelId + "';";
		db.executeUpdate(sql, hotelId, false);
		
		return null;
	}

	@Override
	public Boolean validOnlinePlatform(String hotelId, String userId, String password) {
		if (this.validUser(hotelId, userId, password)!=null) {
			return true;
		}
		return false;
	}

	@Override
	public User validateAccess1(String hotelId, String userId, String password) {
		User user = this.validUser(hotelId, userId, password);

		if(user == null)
			return null;
		if ((user.getUserType().equals(UserType.ADMINISTRATOR.getValue())
						|| user.getUserType().equals(UserType.OWNER.getValue())
						|| user.getUserType().equals(UserType.MANAGER.getValue()))) {

			return user;
		}
		return null;
	}

	@Override
	public User validateAdmin(String hotelId, String userId, String password) {
		
		User user = this.validUser(hotelId, userId, password);

		if(user == null)
			return null;
		if ((user.getUserType().equals(UserType.ADMINISTRATOR.getValue()))) {
			return user;
		}
		return null;
	}

	@Override
	public User validateOwner(String hotelId, String userId, String password) {
		User user = this.validUser(hotelId, userId, password);

		if(user == null)
			return null;
		if ((user.getUserType().equals(UserType.OWNER.getValue()))) {

			return user;
		}
		return null;
	}

	@Override
	public User validateSecretUser(String hotelId, String userId, String password) {
		User user = this.validUser(hotelId, userId, password);

		if(user == null)
			return null;
		if ((user.getUserType().equals(UserType.SECRET.getValue()))) {

			return user;
		}
		return null;
	}

	@Override
	public User validKDSUser(String hotelId, String userId, String password) {
		User user = this.validUser(hotelId, userId, password);
		setAuthToken(userId, hotelId);
		if(user == null)
			return null;

		if (user.getUserType() == UserType.CHEF.getValue() || user.getUserType() == UserType.ADMINISTRATOR.getValue()) 
				return user;
		
		String sql = "UPDATE Users SET authToken = 0, timeStamp = NULL WHERE userId = '" + userId + "'AND hotelId = '"
				+ hotelId + "';";
		db.executeUpdate(sql, hotelId, false);
		return null;
	}

	@Override
	public boolean removeToken(String hotelId, String userId) {

		String sql = "UPDATE Users SET authToken = 0 WHERE userId = '" + userId + "'AND hotelId = '" + hotelId + "';";

		return db.executeUpdate(sql, hotelId, false);
	}

	@Override
	public JSONObject validateToken(String hotelId, String userId, String authToken) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			outObj.put("userType", UserType.UNAUTHORIZED.toString());

			if(userId.equals("")) {
				outObj.put("message", "UserId not found.");
				return outObj;
			}else if(authToken.equals("")) {
				outObj.put("userType", "Token not found. User could not be authenticated.");
				return outObj;
			}
			
			//String sql = "";
			User user = this.getUser(hotelId, userId);
	
			if(hotelId.equals("h0002") || hotelId.equals("h0003")) {
				outObj.put("status", true);
				outObj.put("userType", UserType.getType(user.getUserType()).toString());
			}
			if(user == null) {
				outObj.put("message", "User could not be authenticated.");
			}
			if(user.getUserType() == com.orderon.commons.Designation.ADMINISTRATOR.getValue() 
					|| user.getUserType() == com.orderon.commons.Designation.OWNER.getValue()) {
				if (user.getAuthToken().equals(authToken)) {
					LocalDateTime now = LocalDateTime.now();
					int hourDiff = now.getHour() - LocalDateTime.parse(user.getTimeStamp()).getHour();
		
					// Check if it been 15 minutes since any activity.
					if (hourDiff == 0 || hourDiff == 1) {
						int offset = now.getMinute() - LocalDateTime.parse(user.getTimeStamp()).getMinute();
						offset = offset < 0? offset+15 : offset;
						
						if (offset <= 15 && offset >= 0) {
						
							String sql = "UPDATE Users SET timeStamp = '" + now.toString() + "' WHERE userId = '" + userId
									+ "' AND hotelId = '" + hotelId + "';";
							db.executeUpdate(sql, hotelId, false);
							outObj.put("status", true);
							outObj.put("userType", UserType.getType(user.getUserType()).toString());
							return outObj;
						}else {
							outObj.put("message", "You session  has timed out. Please login again to continue.");
						}
					}else {
						outObj.put("message", "You session  has timed out. Please login again to continue.");
					}
				}
			}else if(user.getUserType() == com.orderon.commons.Designation.MANAGER.getValue()){
				if (user.getAuthToken().equals(authToken)) {
					outObj.put("status", true);
					outObj.put("userType", UserType.getType(user.getUserType()).toString());
				}else {
					outObj.put("message", "Your session has timed out. Please login again to continue.");
				}
			}else {
				outObj.put("status", true);
				outObj.put("userType", UserType.getType(user.getUserType()).toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}
}
