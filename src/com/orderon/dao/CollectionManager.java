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
	public ArrayList<Collection> getCollections(String hotelId) {
		String sql = "SELECT * FROM Collections WHERE hotelId='" + hotelId + "' ORDER BY collectionOrder";
		return db.getRecords(sql, Collection.class, hotelId);
	}

	@Override
	public ArrayList<Collection> getActiveCollections(String hotelId) {
		String sql = "SELECT * FROM Collections WHERE hotelId='" + hotelId + "' AND isActive = 'true' ORDER BY collectionOrder;";
		return db.getRecords(sql, Collection.class, hotelId);
	}

	@Override
	public ArrayList<Collection> getComboCollections(String hotelId) {
		String sql = "SELECT * FROM Collections WHERE hotelId='" + hotelId + "' AND isSpecialCombo = 'true' ORDER BY collectionOrder;";
		return db.getRecords(sql, Collection.class, hotelId);
	}

	@Override
	public JSONObject addCollection(String hotelId, String name, String description, String image,
			 int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, boolean isSpecialCombo, JSONArray tags) {

		JSONObject outObj = new JSONObject();
		try {
			name = name.toUpperCase();
			outObj.put("status", false);
			if(collectionExists(hotelId, name)) {
				outObj.put("message", "CollectionManager Already exists.");
				return outObj;
			}
			
			String sql = "INSERT INTO Collections (hotelId, name, description, image, collectionOrder, hasSubCollection, isActive"
					+ ", isActiveOnZomato, isSpecialCombo, scheduleIds, tags) "
					+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(name) + "', '" + escapeString(description) + "', '" 
					+ (image.equals("No image") ? "" : "1") + "', "+collectionOrder+", '"+hasSubCollection
					+"', 'true', '"+isActiveOnZomato+"', '"+isSpecialCombo+"', '[]', '"+tags+"');";
			
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
	public JSONObject editCollection(int collectionId, String hotelId, String name, String description, boolean isActive,
			int collectionOrder, boolean isActiveOnZomato, boolean hasSubCollection, boolean isSpecialCombo, JSONArray tags) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			
			Collection collection = this.getCollectionById(hotelId, collectionId);
			
			String sql = "UPDATE Collections SET "
					+ "name = '"+name+"', "
					+ "description = '"+description+"', "
					+ "collectionOrder = "+collectionOrder+", "
					+ "hasSubCollection = '"+hasSubCollection+"', "
					+ "isActive = '"+isActive+"',"
					+ "isActiveOnZomato = '"+isActiveOnZomato+"', "
					+ "isSpecialCombo = '"+isSpecialCombo+"', "
					+ "tags = '"+tags+"' "
					+ "WHERE hotelId = '"+hotelId+"' "
					+ "AND id = '"+collectionId+"';";
			
			sql += "UPDATE MenuItems SET collection = '"+name+"' WHERE collection = '"+collection.getName()+"';";
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
	public Boolean collectionExists(String hotelId, String collectionName) {
		Collection collection = getCollectionByName(hotelId, collectionName);
		if (collection != null) {
			return true;
		}
		return false;
	}

	@Override
	public Collection getCollectionByName(String hotelId, String collectionName) {
		String sql = "SELECT * FROM Collections WHERE name='" + escapeString(collectionName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Collection.class, hotelId);
	}

	@Override
	public Collection getCollectionById(String hotelId, int id) {
		String sql = "SELECT * FROM Collections WHERE id=" + id + " AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Collection.class, hotelId);
	}

	@Override
	public boolean deleteCollection(String hotelId, String collectionName) {
		String sql = "DELETE FROM Collections WHERE name = '" + collectionName + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean updateCollectionImageUrl(String outletId, int collectionId, String imageUrl) {
		
		String sql = "UPDATE Collections SET image = '"+imageUrl+"' WHERE id = "+collectionId+";";
		return db.executeUpdate(sql, outletId, true);
	}
}
