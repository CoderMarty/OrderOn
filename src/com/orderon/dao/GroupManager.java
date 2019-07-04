package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IGroup;

public class GroupManager extends AccessManager implements IGroup {

	public GroupManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean groupExists(String hotelId, String groupName) {
		String sql = "SELECT * FROM Groups WHERE hotelId = '"+hotelId+"' AND name = '"+groupName+"';";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public ArrayList<Group> getGroups(String hotelId) {
		String sql = "SELECT * FROM Groups  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Group.class, hotelId);
	}
	
	@Override
	public JSONArray getLast2GroupIds(String hotelId) {
		String sql = "SELECT id FROM Groups WHERE hotelId='" + hotelId + "' ORDER BY id DESC LIMIT 2";
		ArrayList<Group> groups = db.getRecords(sql, Group.class, hotelId);
		JSONArray groupArr = new JSONArray();
		for (Group group : groups) {
			groupArr.put(group.getId());
		}
		return groupArr;
	}

	@Override
	public ArrayList<Group> getActiveGroups(String hotelId) {
		String sql = "SELECT * FROM Groups  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, Group.class, hotelId);
	}

	@Override
	public JSONObject addGroup(String hotelId, String itemIds, String name, String description, int max, int min, 
			Boolean isActive, String title, String subTitle) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
		
			String sql = "INSERT INTO Groups (hotelId, itemIds, name, description, max, min, isActive, title, subTitle) VALUES('" 
					+ escapeString(hotelId) + "', '" + itemIds + "', '" + escapeString(name) + "', '" + escapeString(description) 
					+ "', " + max + ", " + min + ", '" + isActive+ "', '" + title+ "', '" + subTitle + "');";
			if(db.executeUpdate(sql, true)) {
				outObj.put("status", true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean updateGroup(String hotelId, int groupId, String itemIds, String name, String description, 
			int max, int min, Boolean isActive, String title, String subTitle) {

		String sql = "UPDATE Groups SET itemIds = '" + itemIds + "', name = '" + name 
				+ "', description = '" + description + "', max = " + max + ", min = " + min 
				+ ", isActive = '" + isActive + "', title = '" + title + "', subTitle = '" + subTitle 
				+ "' WHERE hotelId = '" + hotelId + "' AND id = " + groupId + ";";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Group getGroupByName(String hotelId, String groupName) {
		String sql = "SELECT * FROM Groups WHERE name='" + escapeString(groupName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Group.class, hotelId);
	}

	@Override
	public Group getGroupById(String hotelId, int groupId) {
		String sql = "SELECT * FROM Groups WHERE id='" + groupId + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Group.class, hotelId);
	}

	@Override
	public boolean deleteGroup(String hotelId, int id) {
		String sql = "DELETE FROM Groups WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}
}
