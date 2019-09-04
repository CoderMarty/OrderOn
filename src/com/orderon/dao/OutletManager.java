package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IOutlet;

public class OutletManager extends AccessManager implements IOutlet {

	public OutletManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JSONObject addOutlet(String corporateId, String restaurantId, String outletId, String name, String companyName, String address,
			String contact, String gstNumber, String vatNumber, String outletCode, String imgLocation, JSONObject location) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
		
			if(this.outletExists(corporateId, outletId)) {
				outObj.put("message", "This outlet already exists. Please login to continue");
				return outObj;
			}
			
			String sql = "INSERT INTO Outlets (corporateId, restaurantId, outletId, name, address, contact, gstNumber, vatNumber, outletCode, "
					+ "imageLocation, location, companyName)"
					+ " VALUES ('" + corporateId + "', '" + restaurantId + "', '" +escapeString(outletId) + "', '" + escapeString(name)
					+ "', '" + escapeString(address) + "', '" + escapeString(contact) + "', '" + escapeString(gstNumber) + "', '" 
					+ escapeString(vatNumber) + "', '" + escapeString(outletCode) + "', '" + escapeString(imgLocation) 
					+ "', '" + location.toString() + "', '" + escapeString(companyName) + "');";
			
			outObj.put("status", db.executeUpdate(sql, outletId, true));
	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public ArrayList<Outlet> getOutletsForCorporate(String corporateId) {
		String sql = "SELECT * FROM Outlets WHERE corporateId = '"+ corporateId + "';";
		return db.getRecords(sql, Outlet.class, corporateId);
	}

	@Override
	public ArrayList<Outlet> getOutletsForSystem(String systemId) {
		String sql = "SELECT * FROM Outlets WHERE systemId = '"+ systemId + "';";
		return db.getRecords(sql, Outlet.class, systemId);
	}
	
	@Override
	public ArrayList<Outlet> getOutlets(String systemId) {
		String sql = "SELECT * FROM Outlets;";
		return db.getRecords(sql, Outlet.class, systemId);
	}

	@Override
	public Outlet getOutlet(String corporateId, String outletId) {
		String sql = "SELECT * FROM Outlets WHERE corporateId = '"+ corporateId + "' AND outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Outlet.class, corporateId);
	}

	@Override
	public Outlet getOutletForSystem(String systemId, String outletId) {
		String sql = "SELECT * FROM Outlets WHERE outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Outlet.class, systemId);
	}

	@Override
	public boolean outletExists(String corporateId, String outletId) {
		String sql = "SELECT * FROM Outlets WHERE corporateId = '"+ corporateId + "' AND outletId = '"+outletId+"';";
		return db.hasRecords(sql, corporateId);
	}

	@Override
	public Settings getSettings(String systemId) {
		String sql = "SELECT * FROM System;";
		return db.getOneRecord(sql, Settings.class, systemId);
	}

	@Override
	public Settings getHotelSettings(String systemId) {
		String sql = "SELECT * FROM Hotel;";
		return db.getOneRecord(sql, Settings.class, systemId);
	}

	@Override
	public boolean isOldVersion(String systemId) {
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='Hotel';";
		return db.hasRecords(sql, systemId);
	}

	@Override
	public ArrayList<EntityString> getOutletsIds(String systemId) {
		String sql = "SELECT outletId AS entityId FROM Outlets;";
		return db.getRecords(sql, EntityString.class, systemId);
	}
}
