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
	public ArrayList<Charge> getCharges(String systemId) {
		String sql = "SELECT * FROM Charges WHERE systemId= '"+systemId+"';";
		return db.getRecords(sql, Charge.class, systemId);
	}
	
	@Override
	public ArrayList<Charge> getOrderCharges(String systemId) {
		String sql = "SELECT * FROM Charges WHERE applicableOn = 'ORDER' AND systemId= '"+systemId+"';";
		return db.getRecords(sql, Charge.class, systemId);
	}

	@Override
	public ArrayList<Charge> getActiveCharges(String systemId) {
		String sql = "SELECT * FROM Charges  WHERE systemId='" + systemId + "' AND isActive = 'true'";
		return db.getRecords(sql, Charge.class, systemId);
	}
	
	@Override
	public ArrayList<Charge> getActiveOrderCharges(String systemId) {
		String sql = "SELECT * FROM Charges WHERE systemId='" + systemId + "' AND isActive = 'true' AND applicableOn = 'ORDER';";
		return db.getRecords(sql, Charge.class, systemId);
	}

	@Override
	public ArrayList<Charge> getChargesForIntegration(String systemId, String outletId) {
		String sql = "SELECT * FROM Charges WHERE outletId='" + outletId + "' AND isApplicableOnline = 'true';";
		return db.getRecords(sql, Charge.class, systemId);
	}

	@Override
	public ArrayList<Charge> getOrderChargesForIntegration(String systemId, String outletId) {
		String sql = "SELECT * FROM Charges WHERE outletId='" + outletId + "' AND isApplicableOnline = 'true' AND applicableOn = 'ORDER';";
		return db.getRecords(sql, Charge.class, systemId);
	}

	@Override
	public boolean addCharge(String corporateId, String systemId, String outletId, String name, BigDecimal value, String type, Boolean isActive,
			String applicableOn, boolean isAlwaysApplicable, BigDecimal minBillAmount, boolean hasTierWiseValues) {

		String sql = "INSERT INTO Charges (corporateId, systemId, outletId, name, value, type, isActive, applicableOn, isAlwaysApplicable, minBillAmount, hasTierWiseValues"
				+ ", taxesOnCharge) VALUES('" + escapeString(corporateId) + "', '"+ escapeString(systemId) + "', '"+ escapeString(outletId) + "', '" 
				+ escapeString(name) + "', " + value + ", '" + type + "', '" + isActive + "', '" + applicableOn + "', '" 
				+ isAlwaysApplicable + "', " + minBillAmount + ", '" + hasTierWiseValues + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Charge getChargeByName(String systemId, String outletId, String taxName) {
		String sql = "SELECT * FROM Charges WHERE name='" + escapeString(taxName) + "' AND systemId='"
				+ escapeString(systemId) + "' AND outletId = '"+outletId+"';";
		return db.getOneRecord(sql, Charge.class, systemId);
	}

	@Override
	public boolean deleteCharge(String systemId, int id) {
		String sql = "DELETE FROM Charges WHERE id = " + id + " AND systemId='" + systemId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Tier> getTiers(String systemId) {
		String sql = "SELECT * FROM Tiers  WHERE systemId='" + systemId + "';";
		return db.getRecords(sql, Tier.class, systemId);
	}

	@Override
	public ArrayList<Tier> getTiers(String systemId, String outletId) {
		String sql = "SELECT * FROM Tiers  WHERE systemId='" + systemId + "' AND outletId = '"+outletId+"';";
		return db.getRecords(sql, Tier.class, systemId);
	}

	@Override
	public boolean addTier(String corporateId, String systemId, String outletId, BigDecimal value, boolean chargeAlwaysApplicable, 
			BigDecimal minBillAMount) {

		String sql = "INSERT INTO Tiers (corporateId, systemId, outletId, value, chargeAlwaysApplicable, minBillAmount) VALUES('" 
				+ escapeString(corporateId) + "', '" + escapeString(systemId) + "', '" + escapeString(outletId) + "', " 
				+ value + ", '" + chargeAlwaysApplicable + "', " + minBillAMount + ");";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Tier getTierById(String systemId, int id) {
		String sql = "SELECT * FROM Tiers WHERE id='" + id + "' AND systemId='"
				+ escapeString(systemId) + "';";
		return db.getOneRecord(sql, Tier.class, systemId);
	}

	@Override
	public boolean deleteTier(String systemId, int id) {
		String sql = "DELETE FROM Tiers WHERE id = " + id + " AND systemId='" + systemId + "';";
		return db.executeUpdate(sql, true);
	}
}
