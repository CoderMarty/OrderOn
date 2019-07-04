package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.ICollection;
import com.orderon.interfaces.ICombo;
import com.orderon.interfaces.IGroup;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.ISubCollection;

public class ComboManager extends AccessManager implements ICombo{

	public ComboManager(Boolean transactionBased) {
		super(transactionBased);
	}

	@Override
	public JSONObject getCombos(String outletId) {
		
		JSONObject outObj = new JSONObject();
		JSONArray collectionsArr = new JSONArray();
		JSONObject collectionObj = null;
		JSONArray subCollectionsArr = new JSONArray();
		JSONObject subCollectionObj = null;
		JSONArray menuItemArr = new JSONArray();
		JSONObject menuItemObj = null;
		JSONArray groupArr = new JSONArray();
		JSONObject groupObj = null;
		
		ArrayList<SubCollection> subCollections = null;
		ArrayList<MenuItem> menuItems = null;
		ArrayList<Collection> collections = null;

		ICollection collectionDao = new CollectionManager(false);
		ISubCollection subCollectionDao = new SubCollectionManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		IGroup groupDao = new GroupManager(false);

		collections = collectionDao.getComboCollections(outletId);
		try {
			outObj = new JSONObject();
			collectionsArr = new JSONArray();
			
			for (Collection collection : collections) {
			
				collectionObj = new JSONObject(collection);
				subCollectionsArr = new JSONArray();
				
				subCollections = subCollectionDao.getSubCollections(outletId, collection.getName());
				
				for (SubCollection subCollection : subCollections) {
					
					subCollectionObj= new JSONObject(subCollection);
					
					menuItems = menuDao.getMenuItems(outletId, collection.getName(), subCollection.getName());
					for (MenuItem menuItem : menuItems) {
						menuItemObj = new JSONObject(menuItem);
						
						groupArr = new JSONArray();
						
						for (int i=0; i< menuItem.getGroups().length(); i++) {
							groupObj = new JSONObject(groupDao.getGroupById(outletId, menuItem.getGroups().getInt(i)));
							groupArr.put(groupObj);
						}
						
						menuItemObj.put("groups", groupArr);
						menuItemArr.put(menuItemObj);
					}
					subCollectionObj.put("menuItems", menuItemArr);
					subCollectionsArr.put(subCollectionObj);
				}
				
				collectionObj.put("subCollections", subCollectionsArr);
				collectionsArr.put(collectionObj);
			}
			outObj.put("combos", collectionsArr);
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outObj;
	}
}
