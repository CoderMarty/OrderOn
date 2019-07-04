package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Collection;

public interface ICollection{

	public ArrayList<Collection> getCollections(String hotelId);
	
	public ArrayList<Collection> getActiveCollections(String hotelId);
	
	public ArrayList<Collection> getComboCollections(String hotelId);
	
	public JSONObject addCollection(String hotelId, String name, String description, String image,
			 int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, boolean isSpecialCombo
			 , JSONArray tags);
	
	public JSONObject editCollection(int collectionId, String hotelId, String name, String description, boolean isActive,
			 int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, boolean isSpecialCombo, JSONArray tags);
	
	public Boolean collectionExists(String hotelId, String collectionName);
	
	public Collection getCollectionByName(String hotelId, String collectionName);
	
	public Collection getCollectionById(String hotelId, int id);
	
	public boolean deleteCollection(String hotelId, String collectionName);
	
	public boolean updateCollectionImageUrl(String outletId, int collectionId, String imageUrl);
}
