package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Charge;

public interface ICharge {
	public ArrayList<Charge> getCharges(String systemId, String outletId);
	
	public ArrayList<Charge> getActiveCharges(String systemId, String outletId);
	
	public ArrayList<Charge> getOrderCharges(String systemId, String outletId);
	
	public ArrayList<Charge> getActiveOrderCharges(String systemId, String outletId);
	
	public ArrayList<Charge> getChargesForIntegration(String systemId, String outletId);
	
	public ArrayList<Charge> getOrderChargesForIntegration(String systemId, String outletId);

	public boolean addCharge(String corporateId, String restaurantId, String systemId, String outletId, String name, BigDecimal value, String type, Boolean isActive,
			String applicableOn, boolean isAlwaysApplicable, BigDecimal minBillAmount, boolean hasTierWiseValues);

	public Charge getChargeByName(String systemId, String outletId, String taxName);

	public boolean deleteCharge(String systemId, int id);
}
