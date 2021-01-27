package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.ICollection;

public class CollectionManager extends AccessManager implements ICollection{

	public CollectionManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Collection> getCollections(String systemId, String outletId) {
		String sql = "SELECT * FROM Collections WHERE outletId = '"+outletId+"' ORDER BY collectionOrder";
		
		return db.getRecords(sql, Collection.class, systemId);
	}

	@Override
	public ArrayList<Collection> getActiveCollections(String systemId, String outletId) {
		String sql = "SELECT * FROM Collections WHERE outletId = '"+ outletId
				+ "' AND isActive = 'true' ORDER BY collectionOrder;";
		return db.getRecords(sql, Collection.class, systemId);
	}

	@Override
	public ArrayList<Collection> getComboCollections(String systemId, String outletId) {
		String sql = "SELECT * FROM Collections WHERE outletId='" + outletId + "' "
				+ "AND isSpecialCombo = 'true' ORDER BY collectionOrder;";
		
		return db.getRecords(sql, Collection.class, systemId);
	}

	@Override
	public JSONObject addCollection(String corparateId, String restaurantId, String systemId, String outletId, String name, 
			String description, String imgUrl, int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, 
			boolean isSpecialCombo , JSONArray tags) {

		JSONObject outObj = new JSONObject();
		try {
			name = name.toUpperCase();
			outObj.put("status", false);
			if(collectionExists(systemId, outletId, name)) {
				outObj.put("message", "CollectionManager Already exists.");
				return outObj;
			}
			
			String sql = "INSERT INTO Collections (corparateId, restaurantId, systemId, outletId, name, description, imgUrl, "
					+ "collectionOrder, hasSubCollection, isActive, isActiveOnZomato, isSpecialCombo, scheduleIds, tags) "
					+ "VALUES('" + escapeString(corparateId) + "', '" + escapeString(restaurantId) + "', '" + escapeString(systemId) 
					+ "', '" + escapeString(outletId) + "', '" + escapeString(name) + "', '" + escapeString(description) + "', '" 
					+ escapeString(imgUrl) + "', '" + "', "+collectionOrder+", '"+hasSubCollection
					+"', 'true', '"+isActiveOnZomato+"', '"+isSpecialCombo+"', '[]', '"+tags+"');";
			
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
	public JSONObject editCollection(int collectionId, String systemId, String outletId, String name, 
			String description, boolean isActive, int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, 
			boolean isSpecialCombo, JSONArray tags, String imgUrl) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			Collection collection = this.getCollectionById(systemId, collectionId);
			
			String sql = "UPDATE Collections SET "
					+ "name = '"+name+"', "
					+ "description = '"+description+"', "
					+ "collectionOrder = "+collectionOrder+", "
					+ "hasSubCollection = '"+hasSubCollection+"', "
					+ "isActive = '"+isActive+"',"
					+ "isActiveOnZomato = '"+isActiveOnZomato+"', "
					+ "isSpecialCombo = '"+isSpecialCombo+"', "
					+ "tags = '"+tags+"', "
					+ "imgUrl = '"+imgUrl+"' "
					+ "WHERE outletId = '"+outletId+"' "
					+ "AND id = '"+collectionId+"';";
			
			sql += "UPDATE MenuItems SET collection = '"+name+"' WHERE collection = '"+collection.getName()
				+"' AND outletId = '"+outletId+"';";
			
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
	public Boolean collectionExists(String systemId, String outletId, String collectionName) {
		Collection collection = getCollectionByName(systemId, outletId, collectionName);
		if (collection != null) {
			return true;
		}
		return false;
	}

	@Override
	public Collection getCollectionByName(String systemId, String outletId, String collectionName) {
		String sql = "SELECT * FROM Collections WHERE name='" + escapeString(collectionName) + "' AND outletId='"
				+ escapeString(outletId) + "';";
		return db.getOneRecord(sql, Collection.class, systemId);
	}

	@Override
	public Collection getCollectionById(String systemId, int collectionId) {
		String sql = "SELECT * FROM Collections WHERE id=" + collectionId + ";";
		return db.getOneRecord(sql, Collection.class, systemId);
	}

	@Override
	public boolean deleteCollection(String systemId, int collectionId) {
		String sql = "DELETE FROM Collections WHERE id = " + collectionId + ";";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public boolean updateCollectionImageUrl(String systemId, int collectionId, String imageUrl) {
		
		String sql = "UPDATE Collections SET imgUrl = '"+imageUrl+"' WHERE id = "+collectionId+";";
		return db.executeUpdate(sql, systemId, true);
	}
}
