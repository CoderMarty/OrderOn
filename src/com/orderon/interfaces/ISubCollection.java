package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.SubCollection;

public interface ISubCollection {
	
	public ArrayList<SubCollection> getSubCollections(String hotelId);
	
	public ArrayList<SubCollection> getActiveSubCollections(String hotelId);
	
	public ArrayList<SubCollection> getSubCollections(String hotelId, String collectionName);

	public boolean addSubCollection(String hotelId, String name, int order, String collection);

	public SubCollection getSubCollectionByName(String hotelId, String subCollectionName);

	public boolean deleteSubCollection(String hotelId, int id);
}
