package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.SubCollection;

public interface ISubCollection {
	
	public ArrayList<SubCollection> getSubCollections(String systemId, String outletId);
	
	public ArrayList<SubCollection> getActiveSubCollections(String systemId, String outletId);
	
	public ArrayList<SubCollection> getSubCollections(String systemId, String outletId, String collectionName);

	public boolean addSubCollection(String corparateId, String restaurantId, String systemId, String outletId, 
			String name, int order, String collection);

	public SubCollection getSubCollectionByName(String systemId, String outletId, String subCollectionName);

	public boolean deleteSubCollection(String systemId, int id);
}
