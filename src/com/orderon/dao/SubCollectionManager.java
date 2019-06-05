package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.ISubCollection;

public class SubCollectionManager extends AccessManager implements ISubCollection {

	public SubCollectionManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<SubCollection> getSubCollections(String hotelId) {
		String sql = "SELECT * FROM SubCollections  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, SubCollection.class, hotelId);
	}

	@Override
	public ArrayList<SubCollection> getActiveSubCollections(String hotelId) {
		String sql = "SELECT * FROM SubCollections  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, SubCollection.class, hotelId);
	}

	@Override
	public ArrayList<SubCollection> getSubCollections(String hotelId, String collectionName) {
		String sql = "SELECT * FROM SubCollections  WHERE hotelId='" + hotelId + "' AND collection = '"+collectionName+"' AND isActive = 'true' ORDER BY subCollectionOrder";
		return db.getRecords(sql, SubCollection.class, hotelId);
	}

	@Override
	public boolean addSubCollection(String hotelId, String name, int order, String collection) {

		String sql = "INSERT INTO SubCollections (hotelId, name, subCollectionOrder, collection) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', " + order + ", '" + collection + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public SubCollection getSubCollectionByName(String hotelId, String subCollectionName) {
		String sql = "SELECT * FROM SubCollections WHERE name='" + escapeString(subCollectionName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, SubCollection.class, hotelId);
	}

	@Override
	public boolean deleteSubCollection(String hotelId, int id) {
		String sql = "DELETE FROM SubCollections WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}
}
