package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.LoyaltySetting;

public interface ILoyaltySettings {

	public ArrayList<LoyaltySetting> getLoyaltySettings(String hotelId);

	public LoyaltySetting getBaseLoyaltySetting(String hotelId);

	public LoyaltySetting getLoyaltySettingByUserType(String hotelId, String userType);

	public Boolean editLoyaltySettings(String hotelId, String userType, int requiredPoints, BigDecimal pointToRupee);
}
