package com.orderon.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.IDiscount;

public class DiscountManager extends AccessManager implements IDiscount{

	public DiscountManager(boolean transactionBased){
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public JSONObject addDiscount(String hotelId, String name, String description, int type, int foodValue, int barValue,
			String startDate, String expiryDate, String usageLimit, JSONArray validColletions, int offerQuantity,
			boolean hasExpiry, String offerType, boolean applicableOnZomato, String bogoItems,
			String startTime, String endTime, int minOrderAmount, boolean firstOrderOnly, 
			int maxFoodDiscountAmount, int maxBarDiscountAmount) throws ParseException {

		JSONObject outObj = new JSONObject();
		String sql = "";
				
		try {
			outObj.put("status", false);
			if(offerType.equals(OFFER_TYPE_BOGO) && !applicableOnZomato) {
				outObj.put("message", "OfferType BOGO cannot be applied on OrderOn. Only on Zomato.");
				return outObj;
			}
			//If zomato offer do the necessary checks.
			if(applicableOnZomato) {
				ArrayList<Discount> discounts = this.getDiscountsForZomato(hotelId);
				for (Discount discount : discounts) {
					if(discount.getOfferType().equals(OFFER_TYPE_BOGO) && offerType.equals(OFFER_TYPE_BOGO)) {
						outObj.put("message", "A BOGO offer cannot co-exist with any other offer on a restaurant at the same time.");
						return outObj;
					}
					sql = "SELECT onlineRate AS entityId FROM MenuItems WHERE hotelId = '"+hotelId+"' AND syncOnZomato = 'true' ORDER BY onlineRate LIMIT 1;";
					EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
					int allowableMinOrderAmount = entity.getId()*2;
					if(allowableMinOrderAmount > minOrderAmount) {
						outObj.put("message", "Min Order Amount should be less than or equal to the Cost For Two for the outlet.");
						return outObj;
					}
					if(type == DISCOUNT_TYPE_PERCENTAGE && (foodValue < 10 || foodValue > 50)) {
						outObj.put("message", "Value should be between 10% to 50% for PERCENTAGE discounts.");
						return outObj;
					}
					if(type == DISCOUNT_TYPE_FIXED && (foodValue < 30)) {
						outObj.put("message", "Value should be at least Rs.30 for RUPEE discounts.");
						return outObj;
					}
					if(barValue > 0) {
						outObj.put("message", "A Zomato offer cannot have a bar value.");
						return outObj;
					}
				}
			}
				
			
			String collections = "";
			for (int i = 0; i < validColletions.length(); i++) {
				try {
					collections += validColletions.getString(i);
					if (i < validColletions.length() - 1) {
						collections += ",";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(usageLimit.equals(""))
				usageLimit = "Unlimited";
			if(!hasExpiry) {
				startDate = "01/01/2018";
				expiryDate = "31/12/3000";
			}
			sql = "INSERT INTO Discount "
					+ "(hotelId, name, description, type, foodValue, barValue, startDate, expiryDate, usageLimit, validCollections, "
					+ "offerType, applicableOnZomato, offerQuantity, bogoItems, startTime, endTime, minOrderAmount, firstOrderOnly, "
					+ "maxFoodDiscountAmount, maxBarDiscountAmount) "
					+ "VALUES('" + escapeString(hotelId) + "', '" + escapeString(name) + "', '" + escapeString(description)
					+ "', '" + Integer.toString(type) + "', " + Integer.toString(foodValue) + ", " + Integer.toString(barValue) + ", '" + startDate
					+ "', '" + expiryDate + "', '" + escapeString(usageLimit) + "', '"
					+ escapeString(collections) + "', '"+offerType+"', '"+applicableOnZomato+"', "+offerQuantity+", '"+bogoItems+"'"
					+ ", '"+startTime+"', '"+endTime+"', "+minOrderAmount+", '"+firstOrderOnly+"', "+maxFoodDiscountAmount+", "+maxBarDiscountAmount+");";
			System.out.println(sql);
			if(db.executeUpdate(sql, true)) {
				outObj.put("status", true);
			}else {
				outObj.put("message", "Discount could not be added. Please try again.");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public JSONObject editDiscount(String hotelId, String name, String description, int type, int foodValue, int barValue, 
			String startDate, String expiryDate, String usageLimit, JSONArray validColletions,  int offerQuantity,
			boolean hasExpiry, String offerType, boolean applicableOnZomato, String bogoItems,
			String startTime, String endTime, int minOrderAmount, boolean firstOrderOnly) throws ParseException {

		JSONObject outObj = new JSONObject();
		String sql = "";
		try {
			outObj.put("status", false);
			if(offerType.equals(OFFER_TYPE_BOGO) && !applicableOnZomato) {
				outObj.put("message", "OfferType BOGO cannot be applied on OrderOn. Only on Zomato.");
				return outObj;
			}
			//If zomato offer do the necessary checks.
			if(applicableOnZomato) {
				ArrayList<Discount> discounts = this.getDiscountsForZomato(hotelId);
				for (Discount discount : discounts) {
					if(discount.getOfferType().equals(OFFER_TYPE_BOGO) && offerType.equals(OFFER_TYPE_BOGO)) {
						outObj.put("message", "A BOGO offer cannot co-exist with any other offer on a restaurant at the same time.");
						return outObj;
					}
					sql = "SELECT onlineRate AS entityId FROM MenuItems WHERE hotelId = '"+hotelId+"' AND syncOnZomato = 'true' ORDER BY onlineRate LIMIT 1;";
					EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
					int allowableMinOrderAmount = entity.getId()*2;
					if(allowableMinOrderAmount > minOrderAmount) {
						outObj.put("message", "Min Order Amount should be less than or equal to the Cost For Two for the outlet.");
						return outObj;
					}
					if(type == DISCOUNT_TYPE_PERCENTAGE && (foodValue < 10 || foodValue > 50)) {
						outObj.put("message", "Value should be between 10% to 50% for PERCENTAGE discounts.");
						return outObj;
					}
					if(type == DISCOUNT_TYPE_FIXED && (foodValue < 30)) {
						outObj.put("message", "Value should be at least Rs.30 for RUPEE discounts.");
						return outObj;
					}
					if(barValue > 0) {
						outObj.put("message", "A Zomato offer cannot have a bar value.");
						return outObj;
					}
				}
			}
			String collections = "";
			for (int i = 0; i < validColletions.length(); i++) {
				try {
					collections += validColletions.getString(i);
					if (i < validColletions.length() - 1) {
						collections += ",";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(usageLimit.equals(""))
				usageLimit = "Unlimited";
			if(!hasExpiry) {
				startDate = "01/01/2018";
				expiryDate = "31/12/3000";
			}
			 sql = "UPDATE Discount SET description = '" + escapeString(description) + "', type = '"
					+ type + "', foodValue = " + foodValue + ", barValue = " + barValue + ", offerQuantity = " + offerQuantity
					+ ", startDate = '" + startDate + "', expiryDate = '" + expiryDate + "', usageLimit = '"
					+ escapeString(usageLimit) + "', validCollections = '" + escapeString(collections)
					+ "', offerType = '"+offerType+"', applicableOnZomato = '"+applicableOnZomato
					+ "', startTime = '"+startTime+"', endTime = '"+endTime
					+ "', minOrderAmount = "+minOrderAmount+", firstOrderOnly = '"+firstOrderOnly
					+ "', bogoItems = '"+bogoItems + "' WHERE  hotelId='" + hotelId + "' AND name = '" + name + "';";
			if(db.executeUpdate(sql, true)) {
				outObj.put("status", true);
			}else {
				outObj.put("message", "Discount could not be edited. Please try again.");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outObj;
	}

	@Override
	public Boolean updateUsageLimit(String hotelId, String name, int usageLimit) {

		String sql = "UPDATE Discount SET usageLimit = '" + Integer.toString(usageLimit) + "' WHERE  hotelId='"
				+ escapeString(hotelId) + "' AND name = '" + name + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Discount> getAllDiscounts(String hotelId) {
		String sql = "SELECT * FROM Discount WHERE hotelId='" + escapeString(hotelId) + "' AND isActive  = 'true';";
		return db.getRecords(sql, Discount.class, hotelId);
	}

	@Override
	public ArrayList<Discount> getDiscountsForZomato(String hotelId) {
		String sql = "SELECT * FROM Discount WHERE hotelId='" + escapeString(hotelId) + "' AND isActive  = 'true' AND applicableOnZomato = 'true';";
		return db.getRecords(sql, Discount.class, hotelId);
	}

	@Override
	public Boolean deleteDiscount(String hotelId, String name) {
		String sql = "UPDATE Discount SET isActive = 'false' WHERE name='" + name + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean discountExists(String hotelId, String name) {
		Discount discount = getDiscountByName(hotelId, name.trim());
		if (discount != null) {
			return true;
		}
		return false;
	}

	@Override
	public Discount getDiscountByName(String hotelId, String name) {
		String sql = "SELECT * FROM Discount WHERE name='" + escapeString(name) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Discount.class, hotelId);
	}

	@Override
	public String getDiscountUsageLimit(String hotelId, String name) {
		String sql = "SELECT usageLimit FROM Discount WHERE name='" + escapeString(name) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";
		Discount dis = db.getOneRecord(sql, Discount.class, hotelId);

		return dis.getUsageLimit();
	}

	@Override
	public BigDecimal getAppliedDiscount(String hotelId, String orderId) {
		String sql = "SELECT (foodDiscount+barDiscount) AS entityId FROM PAYMENT WHERE hotelId = '" + hotelId + "' AND orderId = '"
				+ orderId + "';";

		EntityBigDecimal entity = db.getOneRecord(sql, EntityBigDecimal.class, hotelId);
		if (entity != null) {
			return entity.getId();
		}
		return new BigDecimal("0.0");
	}

	@Override
	public JSONObject applyDiscount(String hotelId, Order order, String discountCode, String discountType) {

		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			boolean status = false;
			Discount discount;
			
			if(discountType.equals(DISCOUNT_TYPE_ZOMATO_VOUCHER) || discountType.equals(DISCOUNT_TYPE_PIGGYBANK) 
					|| discountType.equals(DISCOUNT_TYPE_FIXED_RUPEE_DISCOUNT)) {
				status = this.discountExists(hotelId, discountType);
				discount = this.getDiscountByName(hotelId, discountType);
			}else {
				status = this.discountExists(hotelId, discountCode);
				discount = this.getDiscountByName(hotelId, discountCode);
			}
			if(!status) {
				outObj.put("message", "This code does not exist. Please enter a valid discount code.");
				return outObj;
			}

			if (discount == null) {
				outObj.put("message", "This discount code does not exist.");
				return outObj;
			}
			if (discount.getHasUsageLimit()) {
				if (discount.getUsageLimit().equals("0")) {
					outObj.put("message", "This discount cannot be used right now as it has been exhausted.");
					return outObj;
				}
			}
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date date = df.parse(discount.getExpiryDate());

			if (date.before(new Date())) {
				outObj.put("message", "This discount has expired. Please use another Offer.");
				return outObj;
			}
			date = df.parse(discount.getStartDate());

			if (date.after(new Date())) {
				outObj.put("message", "This discount is not active yet. Please use another Offer.");
				return outObj;
			}
			if(discountType.equals(DISCOUNT_TYPE_DISCOUNT_CODE)){
				JSONArray appliedDiscounts = order.getDiscountCode();
				for (int i=0; i<appliedDiscounts.length(); i++) {
					if(appliedDiscounts.getString(i).equals(discountCode)) {
						outObj.put("message", "This code has already been applied. Can't use the same code twice.");
						return outObj;
					}
				}
			}else {
				if(order.getDiscountCode().toString().contains(discountType)) {
					outObj.put("message", "This code has already been applied. Can't use the same code twice.");
					return outObj;
				}
			}
			JSONArray discountArr = order.getDiscountCode();

			String sql = "";
			if(discountType.equals(DISCOUNT_TYPE_ZOMATO_VOUCHER)) {
				discountArr.put(discountType);
				sql = "UPDATE Orders SET discountCode = '" + discountArr.toString() + "', zomatoVoucherAmount = '" + discountCode
						+ "' WHERE hotelId = '" + hotelId + "' AND orderId = '" + order.getOrderId() + "';";
			}else if(discountType.equals(DISCOUNT_TYPE_PIGGYBANK)) {
				discountArr.put(discountType);
				sql = "UPDATE Orders SET discountCode = '" + discountArr.toString() + "', piggyBank = '" + discountCode
						+ "' WHERE hotelId = '" + hotelId + "' AND orderId = '" + order.getOrderId() + "';";
			}else if(discountType.equals(DISCOUNT_TYPE_FIXED_RUPEE_DISCOUNT)) {
				discountArr.put(discountType);
				sql = "UPDATE Orders SET discountCode = '" + discountArr.toString() + "', fixedRupeeDiscount = '" + discountCode
						+ "' WHERE hotelId = '" + hotelId + "' AND orderId = '" + order.getOrderId() + "';";
			}else if(discountType.equals(DISCOUNT_TYPE_DISCOUNT_CODE)) {
				discountArr.put(discountCode);
				sql = "UPDATE Orders SET discountCode = '" + discountArr.toString() + "' WHERE hotelId = '" + hotelId
						+ "' AND orderId = '" + order.getOrderId() + "';";
			}
			
			if(!db.executeUpdate(sql, true)) {
				outObj.put("message", "This discount code could not be applied. Please contact support.");
				return outObj;
			}
			/*
			JSONArray offerItems = null;
			ArrayList<OrderItem> orderedItemsOnOffer = new ArrayList<OrderItem>();
			int totalOrderedQuantity = 0;
			if(discount.offerType.equals(OFFER_TYPE_BOGO)) {
				//Get all the ordered items in the order
				ArrayList<OrderItem> orderItems = this.getOrderedItems(hotelId, orderId, "rate");
				boolean complimetaryAdded = false;
				int offerQuantity = discount.getOfferQuantity();
				for (OrderItem orderItem : orderItems) {
					if (orderItem.getState() == SUBORDER_STATE_CANCELED || orderItem.getState() == SUBORDER_STATE_COMPLIMENTARY
							|| orderItem.getState() == SUBORDER_STATE_RETURNED || orderItem.getState() == SUBORDER_STATE_VOIDED)
						continue;
					offerItems = discount.getBogoItems();
					//Loop around the offer items.
					for(int i=0; i<offerItems.length(); i++) {
						//If orderItems contains offerItems, add it to the new array.
						if (offerItems.get(i).equals(orderItem.getMenuId())) {
							orderedItemsOnOffer.add(orderItem);
							totalOrderedQuantity += orderItem.getQty();
						}
					}
				}
				for (OrderItem orderedItem : orderedItemsOnOffer) {
					offerQuantity -= orderItem.getQty();
					int qty = offerQuantity <= 0 ? loyalty.getOfferQuantity() : orderItem.getQty();
					complimetaryAdded = this.complimentaryItem(hotelId, orderId, orderItem.getMenuId(), "",
							orderItem.getSubOrderId(), orderItem.getRate(), qty, discount.getName());
					if (!complimetaryAdded) {
						outObj.put("message", "Offer could not be applied. Please contact support. Code 50");
						return outObj;
					}
					if (offerQuantity <= 0)
						break;
				}
			}
			*/
			outObj.put("status", true);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			db.rollbackTransaction();
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			db.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj;
	}

	@Override
	public boolean removeDiscount(String hotelId, String orderId) {

		String sql = "UPDATE Orders SET discountCode = '[]' WHERE hotelId = '" + hotelId
					+ "' AND orderId = '" + orderId + "';";
			
		return db.executeUpdate(sql, true);
	}
}
