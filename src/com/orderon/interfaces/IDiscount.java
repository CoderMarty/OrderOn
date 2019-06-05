package com.orderon.interfaces;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Discount;
import com.orderon.dao.AccessManager.Order;

public interface IDiscount extends IAccess{
	
	public JSONObject addDiscount(String hotelId, String name, String description, int type, int foodValue, int barValue,
			String startDate, String expiryDate, String usageLimit, JSONArray validColletions, int offerQuantity,
			boolean hasExpiry, String offerType, boolean applicableOnZomato, String bogoItems,
			String startTime, String endTime, int minOrderAmount, boolean firstOrderOnly, 
			int maxFoodDiscountAmount, int maxBarDiscountAmount) throws ParseException;

	public JSONObject editDiscount(String hotelId, String name, String description, int type, int foodValue, int barValue, 
			String startDate, String expiryDate, String usageLimit, JSONArray validColletions,  int offerQuantity,
			boolean hasExpiry, String offerType, boolean applicableOnZomato, String bogoItems,
			String startTime, String endTime, int minOrderAmount, boolean firstOrderOnly) throws ParseException;

	public Boolean updateUsageLimit(String hotelId, String name, int usageLimit);

	public ArrayList<Discount> getAllDiscounts(String hotelId);

	public ArrayList<Discount> getDiscountsForZomato(String hotelId);

	public Boolean deleteDiscount(String hotelId, String name);

	public Boolean discountExists(String hotelId, String name);

	public Discount getDiscountByName(String hotelId, String name);

	public String getDiscountUsageLimit(String hotelId, String name);

	public BigDecimal getAppliedDiscount(String hotelId, String orderId);

	public JSONObject applyDiscount(String hotelId, Order order, String discountCode, String discountType);
	
	public boolean removeDiscount(String hotelId, String orderId);
}
