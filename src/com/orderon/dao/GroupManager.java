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
	public Boolean groupExists(String systemId, String outletId, String groupName) {
		String sql = "SELECT * FROM Groups WHERE outletId = '"+outletId+"' AND name = '"+groupName+"';";
		return db.hasRecords(sql, systemId);
	}

	@Override
	public ArrayList<Group> getGroups(String systemId, String outletId) {
		String sql = "SELECT * FROM Groups  WHERE outletId='" + outletId + "'";
		return db.getRecords(sql, Group.class, systemId);
	}
	
	@Override
	public JSONArray getLast2GroupIds(String systemId, String outletId) {
		String sql = "SELECT id FROM Groups WHERE outletId='" + outletId + "' ORDER BY id DESC LIMIT 2;";
		ArrayList<Group> groups = db.getRecords(sql, Group.class, systemId);
		JSONArray groupArr = new JSONArray();
		for (Group group : groups) {
			groupArr.put(group.getId());
		}
		return groupArr;
	}

	@Override
	public ArrayList<Group> getActiveGroups(String systemId, String outletId) {
		String sql = "SELECT * FROM Groups  WHERE outletId='" + outletId + "' AND isActive = 'true';";
		return db.getRecords(sql, Group.class, systemId);
	}

	@Override
	public JSONObject addGroup(String corparateId, String restaurantId, String systemId, String outletId,
			String itemIds, String name, String description, int max, int min, 
			Boolean isActive, String title, String subTitle) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
		
			String sql = "INSERT INTO Groups (corparateId, restaurantId, systemId, outletId, itemIds, name, description, max, min, "
					+ "isActive, isActiveOnline, title, subTitle) VALUES('" + escapeString(corparateId) + "', '" 
					+ escapeString(restaurantId) + "', '"  + escapeString(systemId) + "', '"  + escapeString(outletId) + "', '" 
					+ itemIds + "', '" + escapeString(name) + "', '" + escapeString(description) 
					+ "', " + max + ", " + min + ", '" + isActive+ "', '" + isActive+ "', '" + title+ "', '" + subTitle + "');";
			if(db.executeUpdate(sql, systemId, true)) {
				outObj.put("status", true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean updateGroup(String systemId, String outletId, int groupId, String itemIds, String name, String description, 
			int max, int min, Boolean isActive, Boolean isActiveOnline, String title, String subTitle) {

		String sql = "UPDATE Groups SET itemIds = '" + itemIds + "', name = '" + name 
				+ "', description = '" + description + "', max = " + max + ", min = " + min 
				+ ", isActive = '" + isActive + ", isActiveOnline = '" + isActiveOnline + "', title = '" + title 
				+ "', subTitle = '" + subTitle + "' WHERE outletId = '" + outletId + "' AND id = " + groupId + ";";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Group getGroupByName(String systemId, String outletId, String groupName) {
		String sql = "SELECT * FROM Groups WHERE name='" + escapeString(groupName) + "' AND systemId='"
				+ escapeString(systemId) + "';";
		return db.getOneRecord(sql, Group.class, systemId);
	}

	@Override
	public Group getGroupById(String systemId, int groupId) {
		String sql = "SELECT * FROM Groups WHERE id='" + groupId + "' AND systemId='"
				+ escapeString(systemId) + "';";
		return db.getOneRecord(sql, Group.class, systemId);
	}

	@Override
	public boolean deleteGroup(String systemId, int id) {
		String sql = "DELETE FROM Groups WHERE id = " + id + ";";
		return db.executeUpdate(sql, systemId, true);
	}
}
