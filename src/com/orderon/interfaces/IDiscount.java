package com.orderon.interfaces;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager.Discount;
import com.orderon.dao.AccessManager.Order;

public interface IDiscount extends IAccess{
	
	public JSONObject addDiscount(String corporateId, String restaurantId, String systemId, String outletId, String name, String description, int type, int foodValue, int barValue,
			String startDate, String expiryDate, String usageLimit, JSONArray validColletions, int offerQuantity,
			boolean hasExpiry, String offerType, boolean applicableOnZomato, String bogoItems,
			String startTime, String endTime, int minOrderAmount, boolean firstOrderOnly, 
			int maxFoodDiscountAmount, int maxBarDiscountAmount) throws ParseException;

	public JSONObject editDiscount(String systemId, String outletId, String name, String description, int type, int foodValue, int barValue, 
			String startDate, String expiryDate, String usageLimit, JSONArray validColletions,  int offerQuantity,
			boolean hasExpiry, String offerType, boolean applicableOnZomato, String bogoItems,
			String startTime, String endTime, int minOrderAmount, boolean firstOrderOnly) throws ParseException;

	public Boolean updateUsageLimit(String systemId, int discountId, int usageLimit);

	public ArrayList<Discount> getAllDiscounts(String systemId);

	public ArrayList<Discount> getAllDiscounts(String systemId, String outletId);

	public ArrayList<Discount> getDiscountsForZomato(String systemId, String outletId);

	public Boolean deleteDiscount(String systemId, int discountId);

	public Boolean discountExists(String systemId, String outletId, String name);

	public Discount getDiscountById(String systemId, int id);
	
	public Discount getDiscountByName(String systemId, String outletId, String name);

	public String getDiscountUsageLimit(String systemId, int discountId);

	public BigDecimal getAppliedDiscount(String systemId, String outletId, String orderId);

	public JSONObject applyDiscount(String systemId, String outletId, Order order, String discountCode, String discountType);
	
	public boolean removeAllDiscounts(String systemId, String outletId, String orderId);
	
	public boolean removeDiscount(String systemId, String outletId, String orderId, String discountCode);
}
