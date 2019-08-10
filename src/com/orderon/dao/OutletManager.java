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
	public JSONObject addOutlet(String corporateId, String outletId, String name, String address,
			String contact, String gstNumber, String vatNumber, String outletCode, String fileName) {
		
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
		
			if(this.outletExists(corporateId, outletId)) {
				outObj.put("message", "This outlet already exists. Please login to continue");
				return outObj;
			}
			
			String sql = "INSERT INTO Hotel (corporateId, outletId, name, address, contact, gstNumber, vatNumber, outletCode, imageLocation)"
					+ " VALUES ('" + corporateId + "', '"+escapeString(outletId)+"', '" + escapeString(name)+ "', '" + escapeString(address)+ 
					"', '" + escapeString(contact)+ "', '" + escapeString(gstNumber)+ "', '" + escapeString(vatNumber)+ "', '" + escapeString(outletCode)+ 
					"', '" + escapeString(fileName) + "');";
			
			outObj.put("status", db.executeUpdate(sql, outletId, true));
	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public ArrayList<Outlet> getOutletsForCorporate(String corporateId) {
		String sql = "SELECT * FROM Outlet WHERE corporateId = '"+ corporateId + "';";
		return db.getRecords(sql, Outlet.class, corporateId);
	}
	
	@Override
	public ArrayList<Outlet> getOutlets(String outletId) {
		String sql = "SELECT * FROM Outlet;";
		return db.getRecords(sql, Outlet.class, outletId);
	}

	@Override
	public Outlet getOutlet(String corporateId, String outletId) {
		String sql = "SELECT * FROM Outlet WHERE corporateId = '"+ corporateId + "' AND outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Outlet.class, corporateId);
	}

	@Override
	public Outlet getOutlet(String outletId) {
		String sql = "SELECT * FROM Outlet WHERE outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Outlet.class, outletId);
	}

	@Override
	public boolean outletExists(String corporateId, String outletId) {
		String sql = "SELECT * FROM Outlet WHERE corporateId = '"+ corporateId + "' AND outletId = '"+outletId+"';";
		return db.hasRecords(sql, corporateId);
	}

	@Override
	public Settings getSettings(String outletId) {
		String sql = "SELECT * FROM Hotel WHERE hotelId = '"+outletId+"';";
		return db.getOneRecord(sql, Settings.class, outletId);
	}

	@Override
	public boolean updatePromotionalBalance(String outletId, int sentSmsCount) {
		Settings settings = this.getSettings(outletId);
		int updatedCount = settings.getPromotionalSMSBalance() - sentSmsCount;
		String sql = "UPDATE Hotel SET promotionalSMSBalance = " + updatedCount + " WHERE hotelId = '"+outletId+"';";
		return db.executeUpdate(sql, outletId, true);
	}

	@Override
	public boolean updateTransactionalCount(String outletId, int sentSmsCount) {
		Settings settings = this.getSettings(outletId);
		int updatedCount = settings.getTransactionSMSCount() + sentSmsCount;
		String sql = "UPDATE Hotel SET transactionSMSCount = " + updatedCount + " WHERE hotelId = '"+outletId+"';";
		return db.executeUpdate(sql, outletId, true);
	}
}
