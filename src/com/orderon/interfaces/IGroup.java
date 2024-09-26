package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Group;

public interface IGroup {


	public Boolean groupExists(String systemId, String outletId, String groupName);
	
	public ArrayList<Group> getGroups(String systemId, String outletId);
	
	public ArrayList<Group> getActiveGroups(String systemId, String outletId);

	public JSONObject addGroup(String corparateId, String restaurantId, String systemId, String outletId, String itemIds, 
			String name, String description, int max, int min, Boolean isActive, String title, String subTitle);

	public boolean updateGroup(String systemId, String outletId, int groupId, String itemIds, String name, 
			String description, int max, int min, Boolean isActive, Boolean isActiveOnline, String title, String subTitle);

	public Group getGroupByName(String systemId, String outletId, String groupName);

	public Group getGroupById(String systemId, int groupId);

	public boolean deleteGroup(String systemId, int id);
	
	public JSONArray getLast2GroupIds(String systemId, String outletId);
}
