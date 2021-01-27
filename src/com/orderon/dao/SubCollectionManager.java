package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.ISubCollection;

public class SubCollectionManager extends AccessManager implements ISubCollection {

	public SubCollectionManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<SubCollection> getSubCollections(String systemId, String outletId) {
		String sql = "SELECT * FROM SubCollections  WHERE outletId='" + outletId + "'";
		return db.getRecords(sql, SubCollection.class, systemId);
	}

	@Override
	public ArrayList<SubCollection> getActiveSubCollections(String systemId, String outletId) {
		String sql = "SELECT * FROM SubCollections  WHERE outletId='" + outletId + "' AND isActive = 'true'";
		return db.getRecords(sql, SubCollection.class, systemId);
	}

	@Override
	public ArrayList<SubCollection> getSubCollections(String systemId, String outletId, String collectionName) {
		String sql = "SELECT * FROM SubCollections  WHERE outletId='" + outletId + "' AND collection = '"+collectionName+"' AND isActive = 'true' ORDER BY subCollectionOrder";
		return db.getRecords(sql, SubCollection.class, systemId);
	}

	@Override
	public boolean addSubCollection(String corparateId, String restaurantId, String systemId, String outletId, String name, 
			int order, String collection) {

		String sql = "INSERT INTO SubCollections (corparateId, restaurantId, systemId, outletId, name, subCollectionOrder, "
				+ "collection, isActive) VALUES('" + escapeString(corparateId) + "', '" + escapeString(restaurantId) + "', '"
				+ escapeString(systemId) + "', '" + escapeString(outletId) + "', '"
				+ escapeString(name) + "', " + order + ", '" + collection + "', 'true');";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public SubCollection getSubCollectionByName(String systemId, String outletId, String subCollectionName) {
		String sql = "SELECT * FROM SubCollections WHERE name='" + escapeString(subCollectionName) + "' AND systemId='"
				+ escapeString(systemId) + "';";
		return db.getOneRecord(sql, SubCollection.class, systemId);
	}

	@Override
	public boolean deleteSubCollection(String systemId, int id) {
		String sql = "DELETE FROM SubCollections WHERE id = " + id + ";";
		return db.executeUpdate(sql, systemId, true);
	}
}
