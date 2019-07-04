package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Group;

public interface IGroup {


	public Boolean groupExists(String hotelId, String groupName);
	
	public ArrayList<Group> getGroups(String hotelId);
	
	public ArrayList<Group> getActiveGroups(String hotelId);

	public JSONObject addGroup(String hotelId, String itemIds, String name, 
			String description, int max, int min, Boolean isActive, String title, String subTitle);

	public boolean updateGroup(String hotelId, int groupId, String itemIds, String name, 
			String description, int max, int min, Boolean isActive, String title, String subTitle);

	public Group getGroupByName(String hotelId, String groupName);

	public Group getGroupById(String hotelId, int groupId);

	public boolean deleteGroup(String hotelId, int id);
	
	public JSONArray getLast2GroupIds(String hotelId);
}
