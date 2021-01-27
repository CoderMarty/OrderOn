package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Collection;

public interface ICollection{
	
	public ArrayList<Collection> getCollections(String systemId, String outletId);
	
	public ArrayList<Collection> getActiveCollections(String systemId, String outletId);
	
	public ArrayList<Collection> getComboCollections(String systemId, String outletId);
	
	public JSONObject addCollection(String corparateId, String restaurantId, String systemId, String outletId, String name, 
			String description, String imgUrl, int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, 
			boolean isSpecialCombo , JSONArray tags);
	
	public JSONObject editCollection(int collectionId, String systemId, String outletId, String name, 
			String description, boolean isActive, int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, 
			boolean isSpecialCombo, JSONArray tags, String imgUrl);
	
	public Boolean collectionExists(String systemId, String outletId, String collectionName);
	
	public Collection getCollectionByName(String systemId, String outletId, String collectionName);
	
	public Collection getCollectionById(String systemId, int id);
	
	public boolean deleteCollection(String systemId, int id);
	
	public boolean updateCollectionImageUrl(String systemId, int collectionId, String imageUrl);
}
