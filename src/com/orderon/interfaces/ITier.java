package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.Tier;

public interface ITier {
	public ArrayList<Tier> getTiers(String hotelId);

	public boolean addTier(String hotelId, BigDecimal value, boolean chargeAlwaysApplicable, BigDecimal minBillAMount);

	public Tier getTierById(String hotelId, int id);

	public boolean deleteTier(String hotelId, int id);
}
