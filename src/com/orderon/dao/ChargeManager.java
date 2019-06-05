package com.orderon.dao;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.interfaces.ICharge;
import com.orderon.interfaces.ITier;

public class ChargeManager extends AccessManager implements ICharge, ITier {

	public ChargeManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ArrayList<Charge> getCharges(String hotelId) {
		String sql = "SELECT * FROM Charges WHERE hotelId= '"+hotelId+"';";
		return db.getRecords(sql, Charge.class, hotelId);
	}
	
	@Override
	public ArrayList<Charge> getOrderCharges(String hotelId) {
		String sql = "SELECT * FROM Charges WHERE applicableOn = 'ORDER' AND hotelId= '"+hotelId+"';";
		return db.getRecords(sql, Charge.class, hotelId);
	}

	@Override
	public ArrayList<Charge> getActiveCharges(String hotelId) {
		String sql = "SELECT * FROM Charges  WHERE hotelId='" + hotelId + "' AND isActive = 'true'";
		return db.getRecords(sql, Charge.class, hotelId);
	}
	
	@Override
	public ArrayList<Charge> getActiveOrderCharges(String hotelId) {
		String sql = "SELECT * FROM Charges WHERE hotelId='" + hotelId + "' AND isActive = 'true' AND applicableOn = 'ORDER';";
		return db.getRecords(sql, Charge.class, hotelId);
	}

	@Override
	public ArrayList<Charge> getChargesForIntegration(String hotelId) {
		String sql = "SELECT * FROM Charges WHERE hotelId='" + hotelId + "' AND isApplicableOnline = 'true';";
		return db.getRecords(sql, Charge.class, hotelId);
	}

	@Override
	public ArrayList<Charge> getOrderChargesForIntegration(String hotelId) {
		String sql = "SELECT * FROM Charges WHERE hotelId='" + hotelId + "' AND isApplicableOnline = 'true' AND applicableOn = 'ORDER';";
		return db.getRecords(sql, Charge.class, hotelId);
	}

	@Override
	public boolean addCharge(String hotelId, String name, BigDecimal value, String type, Boolean isActive,
			String applicableOn, boolean isAlwaysApplicable, BigDecimal minBillAmount, boolean hasTierWiseValues) {

		String sql = "INSERT INTO Charges (hotelId, name, value, type, isActive, applicableOn, isAlwaysApplicable, minBillAmount, hasTierWiseValues"
				+ ", taxesOnCharge) VALUES('" + escapeString(hotelId) + "', '" + escapeString(name) + "', " + value + ", '" + type + "', '" 
				+ isActive + "', '" + applicableOn + "', '" + isAlwaysApplicable + "', " + minBillAmount + ", '" + hasTierWiseValues + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Charge getChargeByName(String hotelId, String taxName) {
		String sql = "SELECT * FROM Charges WHERE name='" + escapeString(taxName) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Charge.class, hotelId);
	}

	@Override
	public boolean deleteCharge(String hotelId, int id) {
		String sql = "DELETE FROM Charges WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Tier> getTiers(String hotelId) {
		String sql = "SELECT * FROM Tiers  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Tier.class, hotelId);
	}

	@Override
	public boolean addTier(String hotelId, BigDecimal value, boolean chargeAlwaysApplicable, BigDecimal minBillAMount) {

		String sql = "INSERT INTO Tiers (hotelId, value, chargeAlwaysApplicable, minBillAmount) VALUES('" + escapeString(hotelId) 
				+ "', " + value + ", '" + chargeAlwaysApplicable + "', " + minBillAMount + ");";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Tier getTierById(String hotelId, int id) {
		String sql = "SELECT * FROM Tiers WHERE id='" + id + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Tier.class, hotelId);
	}

	@Override
	public boolean deleteTier(String hotelId, int id) {
		String sql = "DELETE FROM Tiers WHERE id = " + id + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}
}
