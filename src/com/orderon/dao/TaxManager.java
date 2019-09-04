package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.interfaces.ITax;

public class TaxManager extends AccessManager implements ITax{

	public TaxManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Tax> getTaxes(String systemId, String outletId) {
		String sql = "SELECT * FROM Taxes  WHERE outletId='" + outletId + "' AND applicableOn != 'INVENTORY';";
		return db.getRecords(sql, Tax.class, systemId);
	}

	@Override
	public ArrayList<Tax> getActiveTaxes(String systemId, String outletId) {
		String sql = "SELECT * FROM Taxes  WHERE outletId='" + outletId + "' AND isActive = 'true' AND applicableOn != 'INVENTORY';";
		return db.getRecords(sql, Tax.class, systemId);
	}
	
	@Override
	public ArrayList<Tax> getTaxesForMaterials(String systemId, String outletId) {
		String sql = "SELECT * FROM Taxes  WHERE outletId='" + outletId + "' AND applicableOn = 'INVENTORY';";
		return db.getRecords(sql, Tax.class, systemId);
	}

	@Override
	public boolean addTax(String corporateId, String restaurantId, String systemId, String outletId, String name, 
			BigDecimal value, String type, Boolean isActive) {

		String sql = "INSERT INTO Taxes (corporateId, restaurantId, systemId, outletId, name, value, type, isActive) VALUES('" 
				+ corporateId + "', '" + restaurantId + "', '" + systemId + "', '" + outletId + "', '" 
				+ escapeString(name) + "', " + value + ", '" + type + "', '" + isActive + "');";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Tax getTaxByName(String systemId, String outletId, String taxName) {
		String sql = "SELECT * FROM Taxes WHERE name='" + escapeString(taxName) + "' AND systemId='"
				+ escapeString(systemId) + "' AND outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Tax.class, systemId);
	}

	@Override
	public boolean deleteTax(String systemId, int id) {
		String sql = "DELETE FROM Taxes WHERE id = " + id + ";";
		return db.executeUpdate(sql, systemId, true);
	}
}
