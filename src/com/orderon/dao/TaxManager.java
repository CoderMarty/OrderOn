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
	public ArrayList<Tax> getTaxes(String hotelId) {
		String sql = "SELECT * FROM Taxes  WHERE hotelId='" + hotelId + "' AND applicableOn != 'INVENTORY';";
		return db.getRecords(sql, Tax.class, hotelId);
	}

	@Override
	public ArrayList<Tax> getActiveTaxes(String hotelId) {
		String sql = "SELECT * FROM Taxes  WHERE hotelId='" + hotelId + "' AND isActive = 'true' AND applicableOn != 'INVENTORY';";
		return db.getRecords(sql, Tax.class, hotelId);
	}
	
	@Override
	public ArrayList<Tax> getTaxesForMaterials(String hotelId) {
		String sql = "SELECT * FROM Taxes  WHERE hotelId='" + hotelId + "' AND applicableOn = 'INVENTORY';";
		return db.getRecords(sql, Tax.class, hotelId);
	}

	@Override
	public boolean addTax(String hotelId, String name, BigDecimal value, String type, Boolean isActive) {

		String sql = "INSERT INTO Taxes (hotelId, name, value, type, isActive) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', " + value + ", '" + type + "', '" + isActive + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Tax getTaxByName(String hotelId, String taxName) {
		String sql = "SELECT * FROM Taxes WHERE name='" + escapeString(taxName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Tax.class, hotelId);
	}

	@Override
	public boolean deleteTax(String hotelId, int id) {
		String sql = "DELETE FROM Taxes WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}
}
