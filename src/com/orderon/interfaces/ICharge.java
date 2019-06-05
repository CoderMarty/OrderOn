package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Charge;

public interface ICharge {
	public ArrayList<Charge> getCharges(String hotelId);
	
	public ArrayList<Charge> getActiveCharges(String hotelId);
	
	public ArrayList<Charge> getOrderCharges(String hotelId);
	
	public ArrayList<Charge> getActiveOrderCharges(String hotelId);
	
	public ArrayList<Charge> getChargesForIntegration(String hotelId);
	
	public ArrayList<Charge> getOrderChargesForIntegration(String hotelId);

	public boolean addCharge(String hotelId, String name, BigDecimal value, String type, Boolean isActive,
			String applicableOn, boolean isAlwaysApplicable, BigDecimal minBillAmount, boolean hasTierWiseValues);

	public Charge getChargeByName(String hotelId, String taxName);

	public boolean deleteCharge(String hotelId, int id);
}
