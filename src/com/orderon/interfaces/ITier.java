package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Tier;

public interface ITier {
	
	public ArrayList<Tier> getTiers(String systemId);

	public ArrayList<Tier> getTiers(String systemId, String outletId);

	public boolean addTier(String corporateId, String systemId, String outletId, BigDecimal value, boolean chargeAlwaysApplicable, 
			BigDecimal minBillAMount);

	public Tier getTierById(String systemId, int id);

	public boolean deleteTier(String systemId, int id);
}
